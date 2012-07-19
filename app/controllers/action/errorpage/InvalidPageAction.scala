package controllers.action.errorpage

import controllers.action.AbstractAction
import controllers.ActionContext
import in.partake.resource.ServerErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import in.partake.resource.UserErrorCode

class InvalidPageActionParams(val errorCode: Option[UserErrorCode])
class InvalidPageActionValues(val errorCode: Option[UserErrorCode])

object InvalidPageAction extends AbstractAction[InvalidPageActionParams, InvalidPageActionValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): InvalidPageActionParams = {
    val errorCode = paramFromQueryString("errorCode", request) match {
      case None => None
      case Some(x) => Option(UserErrorCode.safeValueOf(x))
    }

    new InvalidPageActionParams(errorCode)
  }

  override def executeAction(params: InvalidPageActionParams)(implicit context: ActionContext): InvalidPageActionValues = {
    new InvalidPageActionValues(params.errorCode)
  }

  override def renderResult(values: InvalidPageActionValues)(implicit context: ActionContext): PlainResult = {
    render(views.html.error.invalid.render(values.errorCode, context))
  }
}
