package controllers.api
import controllers.AbstractController
import in.partake.resource.UserErrorCode
import play.api.mvc.PlainResult
import play.api.libs.json.JsValue
import play.api.Logger
import in.partake.resource.ServerErrorCode
import play.api.libs.json.Json
import controllers.ActionContext
import scala.collection.immutable.Map
import play.api.libs.json.JsObject
import play.api.libs.json.JsObject

abstract class AbstractAPI[S, T] extends AbstractController[S, T] {

  def renderJson(obj: JsValue, status: Int): PlainResult = {
    Status(status)(obj).withHeaders(
      CACHE_CONTROL -> "no-cache"
    )
  }

  def renderOK(): PlainResult = {
    renderJson(Json.toJson(Map(
        "result" -> "ok"
    )), OK)
  }

  def renderOK(obj: JsObject): PlainResult = {
    val json = obj ++ Json.toJson(Map("result" -> "ok")).asInstanceOf[JsObject]
    renderJson(json, OK)
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

    renderJson(obj, INTERNAL_SERVER_ERROR)
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
