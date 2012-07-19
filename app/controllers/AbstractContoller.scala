package controllers
import java.util.UUID
import play.api.Play.current
import play.api.db.DB
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.PlainResult
import play.api.Logger
import in.partake.controller.PartakeActionContext
import in.partake.resource.Constants
import in.partake.model.UserEx
import in.partake.resource.UserErrorCode
import in.partake.resource.ServerErrorCode
import in.partake.resource.MessageCode
import in.partake.base.PartakeException
import in.partake.model.dao.DAOException
import in.partake.model.access.DBAccess
import in.partake.model.dao.PartakeConnection
import in.partake.model.IPartakeDAOs
import in.partake.model.daofacade.UserDAOFacade

abstract class AbstractController[S, T] extends Controller {

  def run = Action { request: Request[AnyContent] =>
    val beginTime = System.currentTimeMillis()

    implicit val context = prepare(request)
    val result: PlainResult = try {
      val params = parseRequest(request)
      val t = executeAction(params)
      renderResult(t)
    } catch {
      case e: DAOException =>
        renderError(ServerErrorCode.DB_ERROR, e)
      case e: PartakeException =>
        renderException(e)
      case e: Exception =>
        renderError(ServerErrorCode.UNKNOWN_ERROR, e)
    } finally {
      val endTime = System.currentTimeMillis()
      Logger.info(request.uri + " took " + (endTime - beginTime) + "[msec] to process.")
    }

    finalizeResult(result)
  }

  def prepare(request: Request[AnyContent]): ActionContext = {
    val userId: Option[String] = request.session.get(Constants.Session.USER_ID_KEY)
    val user: Option[UserEx] = userId match {
      case None => None
      case Some(id) =>
        Option(new DBAccess[UserEx] {
          override protected def doExecute(con: PartakeConnection, daos: IPartakeDAOs): UserEx = {
            return UserDAOFacade.getUserEx(con, daos, id);
          }
        }.execute())
    }

    val currentURL: String = request.uri

    var sessionsToBeAdded: List[(String, String)] = List.empty

    val sessionToken: String = request.session.get(Constants.Session.TOKEN_KEY) match {
      case None =>
        var token = UUID.randomUUID().toString()
        sessionsToBeAdded = (Constants.Session.TOKEN_KEY -> token) :: sessionsToBeAdded
        token
      case Some(x) => x
    }

    val sessionId: String = request.session.get(Constants.Session.ID_KEY) match {
      case None =>
        val id = UUID.randomUUID().toString()
        sessionsToBeAdded = (Constants.Session.ID_KEY -> UUID.randomUUID().toString()) :: sessionsToBeAdded
        id
      case Some(x) => x
    }

    val context = new ActionContext(user, sessionToken, currentURL)

    for (kv <- sessionsToBeAdded)
      context.shouldAddToSession(kv._1, kv._2)

    return context
  }

  def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): S
  def executeAction(params: S)(implicit context: ActionContext): T
  def renderResult(values: T)(implicit context: ActionContext): PlainResult

  def finalizeResult(result: PlainResult)(implicit context: ActionContext): PlainResult =
    result.withSession(context.sessionsToAddResult: _*).withHeaders(context.headers: _*)

  // ----------------------------------------------------------------------
  // parameter

  def paramFromQueryString(key: String, request: Request[AnyContent])(implicit context: ActionContext) = {
    request.queryString.get(key) match {
      case None | Some(Seq()) => None
      case Some(Seq(x, _*)) => Some(x)
    }
  }

  // ----------------------------------------------------------------------
  // renderSomething

  protected def renderInvalid(ec: UserErrorCode, e: Option[Throwable] = None): PlainResult
  protected def renderError(ec: ServerErrorCode, e: Option[Throwable] = None): PlainResult
  protected def renderLoginRequired()(implicit context: ActionContext): PlainResult
  protected def renderForbidden(): PlainResult
  protected def renderNotFound(): PlainResult

  protected def renderInvalid(ec: UserErrorCode, e: Throwable): PlainResult = renderInvalid(ec, Option(e))
  protected def renderError(ec: ServerErrorCode, e: Throwable): PlainResult = renderError(ec, Option(e))

  protected def renderRedirect(url: String): PlainResult =
    renderRedirect(url, None)

  protected def renderRedirect(url: String, code: MessageCode): PlainResult =
    renderRedirect(url, Option(code))

  protected def renderRedirect(url: String, code: Option[MessageCode]): PlainResult = {
    code match {
      case None => Redirect(url)
      case Some(c) => Redirect(url).flashing(
        Constants.Flash.MESSAGE_ID -> c.getErrorCode() // TOOD: getErrorCode should be renamed.
      )
    }
  }

  protected def renderException(e: PartakeException)(implicit context: ActionContext): PlainResult = {
    e.getStatusCode() match {
      case 401 =>
        return renderLoginRequired()
      case 403 =>
        return renderForbidden()
      case 404 =>
        return renderNotFound()
    }

    if (e.getServerErrorCode() != null)
      return renderError(e.getServerErrorCode(), e.getCause())
    else
      return renderInvalid(e.getUserErrorCode(), e.getCause())
  }
}

