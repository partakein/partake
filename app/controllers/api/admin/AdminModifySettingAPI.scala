package controllers.api.admin;

import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ConfigurationItem;
import in.partake.resource.ConfigurationKeyConstants;
import in.partake.resource.UserErrorCode;

import controllers.api.AbstractAPI
import controllers.ActionContext
import models.db.Transaction

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult
import play.api.mvc.Result

object AdminModifySettingAPI extends AbstractAPI[ConfigurationItem, Unit] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): ConfigurationItem = {
    ensureAdmin()
    ensureValidSessionToken(request)

    val key: String = paramFromForm("key", request) match {
      case None => throw new PartakeException(UserErrorCode.INVALID_PARAMETERS)
      case Some(x) => x
    }
    val value: String = paramFromForm("value", request) match {
      case None => throw new PartakeException(UserErrorCode.INVALID_PARAMETERS)
      case Some(x) => x
    }

    if (!ConfigurationKeyConstants.configurationkeySet.contains(key))
      throw new PartakeException(UserErrorCode.INVALID_ADMIN_SETTING_KEY)

    return new ConfigurationItem(key, value)
  }

  override def executeAction(params: ConfigurationItem)(implicit context: ActionContext): Unit = {
    Transaction { (con, daos) =>
      daos.getConfiguraitonItemAccess().put(con, params)
    }
  }

  override def renderResult(values: Unit)(implicit context: ActionContext): PlainResult = {
    renderOK()
  }
}

