package controllers.api
import controllers.AbstractController
import in.partake.resource.UserErrorCode
import play.api.mvc.PlainResult
import play.api.libs.json.JsValue
import play.api.Logger
import in.partake.resource.ServerErrorCode
import play.api.libs.json.Json
import controllers.ActionContext

abstract class AbstractAPI extends AbstractController {

  def renderJson(obj: JsValue, status: Int): PlainResult = {
    Status(status)(obj)
  }

  override protected def renderInvalid(ec: UserErrorCode, e: Option[Throwable]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.info("renderInvalid", x)
    }

    val obj = Json.toJson(Map(
        "result" -> "invalid",
        "reason" -> ec.getReasonString()
    ))

    renderJson(obj, BAD_REQUEST)
  }

  override protected def renderError(ec: ServerErrorCode, e: Option[Throwable]): PlainResult = {
    e match {
      case None => ()
      case Some(x) => Logger.warn("renderError", x)
    }

    val obj = Json.toJson(Map(
        "result" -> "error",
        "reason" -> ec.getReasonString()
    ))

    renderJson(obj, BAD_REQUEST)
  }

  override protected def renderLoginRequired()(implicit context: ActionContext): PlainResult = {
    val obj = Json.toJson(Map(
        "result" -> "auth",
        "reason" -> "login is required"
    ))

    context.addHeader("WWW-Authenticate", "OAuth")
    renderJson(obj, UNAUTHORIZED)
  }

  override protected def renderForbidden(): PlainResult = {
    val obj = Json.toJson(Map(
        "result" -> "forbidden",
        "reason" -> "forbidden action"
    ))

    renderJson(obj, FORBIDDEN)
  }

  override protected def renderNotFound(): PlainResult = {
    val obj = Json.toJson(Map(
        "result" -> "notfound",
        "reason" -> "not found"
    ))

    renderJson(obj, NOT_FOUND)
  }
}
