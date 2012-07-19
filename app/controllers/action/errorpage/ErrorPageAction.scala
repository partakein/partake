package controllers.action.errorpage

import controllers.action.AbstractAction
import controllers.ActionContext
import in.partake.resource.ServerErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

class ErrorPageActionParams(val errorCode: Option[ServerErrorCode])
class ErrorPageActionValues(val errorCode: Option[ServerErrorCode])

object ErrorPageAction extends AbstractAction[ErrorPageActionParams, ErrorPageActionValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): ErrorPageActionParams = {
    val errorCode = paramFromQueryString("errorCode", request) match {
      case None => None
      case Some(x) => Option(ServerErrorCode.safeValueOf(x))
    }

    new ErrorPageActionParams(errorCode)
  }

  override def executeAction(params: ErrorPageActionParams)(implicit context: ActionContext): ErrorPageActionValues = {
    new ErrorPageActionValues(params.errorCode)
  }

  override def renderResult(values: ErrorPageActionValues)(implicit context: ActionContext): PlainResult = {
    render(views.html.error.error.render(values.errorCode, context))
  }
}
