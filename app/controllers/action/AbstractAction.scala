package controllers.action
import controllers.AbstractController
import in.partake.resource.UserErrorCode
import in.partake.resource.ServerErrorCode
import play.api.mvc.PlainResult
import play.api.Logger
import play.api.templates.Html
import controllers.ActionContext

abstract class AbstractAction[S, T] extends AbstractController[S, T] {
  protected def render(content: Html): PlainResult = Ok(content)

  override protected def renderInvalid(ec: UserErrorCode, e: Option[Throwable] = None): PlainResult = {
    e match {
      case None =>
        renderRedirect("/invalid")
      case Some(x) =>
        Logger.debug("renderInvalid", x)
        renderRedirect("/invalid?errorCode=" + ec.getErrorCode())
    }
  }

  override protected def renderError(ec: ServerErrorCode, e: Option[Throwable] = None): PlainResult = {
    e match {
      case None =>
        renderRedirect("/error")
      case Some(x) =>
        Logger.info("renderError", x);
        Redirect("/error?errorCode=" + ec.getErrorCode())
    }
  }

  override protected def renderLoginRequired()(implicit context: ActionContext): PlainResult =
    renderRedirect("/loginRequired")

  override protected def renderForbidden(): PlainResult =
    renderRedirect("/forbidden")

  override protected def renderNotFound(): PlainResult =
    renderRedirect("/notfound")
}
