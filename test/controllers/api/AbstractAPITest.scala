package controllers.api

import controllers.AbstractControllerTest
import in.partake.resource.UserErrorCode
import in.partake.resource.ServerErrorCode
import play.api.http.HeaderNames
import play.api.test.Helpers
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.libs.json.JsString
import org.apache.commons.lang.StringUtils

abstract class AbstractAPITest extends AbstractControllerTest {

  // ----------------------------------------------------------------------
  // Result Expectation (2xx)

  // 200 OK
  protected def expectResultOK(result: Result): Unit = {
    assertResult(Helpers.OK) { Helpers.status(result) }
    expectBehaveAsApi(result)
  }

  // ----------------------------------------------------------------------
  // Result Exceptation (4xx)

  // 400 Bad Request
  protected def expectResultInvalid(result: Result, ec: UserErrorCode): Unit = {
    assertResult(Helpers.BAD_REQUEST) { Helpers.status(result) }
    expectBehaveAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }

  // 401 Unauthorized
  protected def expectResultLoginRequired(result: Result): Unit = {
    assertResult(Helpers.UNAUTHORIZED) { Helpers.status(result) }
    expectBehaveAsApi(result)

    assertResult(Some("OAuth")) { Helpers.header("WWW-Authenticate", result) }

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult("auth") { (json \ "result").asInstanceOf[JsString].value }
  }

  // 403 Forbidden
  protected def expectResultForbidden(result: Result): Unit = {
    assertResult(Helpers.FORBIDDEN) { Helpers.status(result) }
    expectBehaveAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult("forbidden") { (json \ "result").asInstanceOf[JsString].value }
    assertResult(false) { StringUtils.isBlank((json \ "reason").asInstanceOf[JsString].value) }
  }

  // ----------------------------------------------------------------------
  // Result Exceptation (5xx)

  // 500 Internal Server Error
  protected def expectResultError(result: Result, ec: ServerErrorCode): Unit = {
    assertResult(Helpers.INTERNAL_SERVER_ERROR) { Helpers.status(result) }
    expectBehaveAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }

  // ----------------------------------------------------------------------
  // Shared

  private def expectBehaveAsApi(result: Result): Unit = {
    assertResult(Some("application/json; charset=utf-8")) {
      Helpers.header(HeaderNames.CONTENT_TYPE, result)
    }
    assertResult(Some("no-cache")) {
      Helpers.header(HeaderNames.CACHE_CONTROL, result)
    }
  }
}
