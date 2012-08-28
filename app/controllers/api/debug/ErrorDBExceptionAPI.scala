package controllers.api.debug

import controllers.api.AbstractAPI
import controllers.ActionContext
import in.partake.model.dao.DAOException
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

/**
 * DAOException が不意に起こった場合。
 */
object ErrorDBExceptionAPI extends AbstractAPI[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    throw new DAOException();
  }
}
