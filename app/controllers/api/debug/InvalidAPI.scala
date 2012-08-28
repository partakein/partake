package controllers.api.debug

import controllers.api.AbstractAPI
import controllers.ActionContext
import in.partake.resource.UserErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

object InvalidAPI extends AbstractAPI[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    return renderInvalid(UserErrorCode.INTENTIONAL_USER_ERROR);
  }
}
