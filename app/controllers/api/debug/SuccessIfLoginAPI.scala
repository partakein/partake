package controllers.api.debug

import controllers.api.AbstractAPI
import controllers.ActionContext
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

/**
 * login していれば success と同じ挙動をする。
 * そうでなければ loginRequired を返し、HTTP status は 401 を返す。
 * 401 は WWW-Authentication をふくまねばならないので、とりあえず OAuth を入れておく。
 */
object SuccessIfLoginAPI extends AbstractAPI[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    context.loginUser match {
      case None =>
        return renderLoginRequired()
      case Some(_) =>
        return renderOK()
    }
  }
}
