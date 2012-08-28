package controllers.api.debug

import controllers.api.AbstractAPI
import controllers.ActionContext
import in.partake.resource.ServerErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

/**
 * データベースエラー。
 * ステータスは 500 を返す。
 */
object ErrorDBAPI extends AbstractAPI[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    return renderError(ServerErrorCode.DB_ERROR);
  }
}
