package controllers.action.errorpage

import controllers.action.AbstractAction
import controllers.ActionContext
import in.partake.resource.ServerErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

object NotFoundPageAction extends AbstractAction[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    render(views.html.error.notfound.render(context))
  }
}
