package controllers.api.debug

import controllers.api.AbstractAPI
import controllers.ActionContext
import in.partake.base.PartakeException
import in.partake.resource.UserErrorCode
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.PlainResult

class EchoAPIParams(val data: String)
class EchoAPIValues(val data: String)

/**
 * data を読んで、それを echo して返す。
 * data があれば 200 を返し、なければ 400 を返す。
 */
object EchoAPI extends AbstractAPI[EchoAPIParams, EchoAPIValues] {
  override def parseRequest(request: Request[AnyContent])(implicit context: ActionContext): EchoAPIParams = {
    val data = request.method match {
      case "GET" => paramFromQueryString("data", request)
      case "POST" => paramFromForm("data", request)
    }

    data match {
      case None => throw new PartakeException(UserErrorCode.INVALID_ARGUMENT)
      case Some(data: String) => new EchoAPIParams(data)
    }
  }

  override def executeAction(params: EchoAPIParams)(implicit context: ActionContext): EchoAPIValues = {
    new EchoAPIValues(params.data)
  }

  override def renderResult(values: EchoAPIValues)(implicit context: ActionContext): PlainResult = {
    val obj = Json.toJson(Map("data" -> values.data)).asInstanceOf[JsObject]
    renderOK(obj);
  }
}
