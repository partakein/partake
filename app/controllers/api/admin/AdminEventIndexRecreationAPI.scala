package controllers.api.admin

import in.partake.app.PartakeApp
import in.partake.base.PartakeException
import in.partake.model.IPartakeDAOs
import in.partake.model.dao.DAOException
import in.partake.model.dao.PartakeConnection
import in.partake.model.daofacade.EventDAOFacade
import in.partake.service.IEventSearchService

import controllers.api.AbstractAPI
import controllers.ActionContext
import models.db.Transaction

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Result

object AdminEventIndexRecreationAPI extends AbstractAPI[Unit, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): Unit = {
    ensureAdmin()
    ensureValidSessionToken(request)
  }

  override def executeAction(params: Unit)(implicit context: ActionContext): Unit = {
    Transaction { (con, daos) =>
      val searchService: IEventSearchService = PartakeApp.getEventSearchService()
      EventDAOFacade.recreateEventIndex(con, daos, searchService)
    }
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    renderOK()
  }
}

