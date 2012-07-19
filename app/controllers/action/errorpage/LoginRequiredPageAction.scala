package controllers.action.errorpage

import controllers.action.AbstractAction
import controllers.ActionContext
import in.partake.resource.ServerErrorCode
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

object LoginRequiredPageAction extends AbstractAction[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    // If a user already has logged in, redirect to the top page.
    context.loginUser match {
      case None =>
        render(views.html.error.loginRequired.render(context))
      case Some(_) =>
        renderRedirect("/")
    }
  }
}
