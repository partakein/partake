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
    expect(Helpers.OK) { Helpers.status(result) }
    expectHabitAsApi(result)
  }

  // ----------------------------------------------------------------------
  // Result Exceptation (4xx)

  // 400 Bad Request
  protected def expectResultInvalid(result: Result, ec: UserErrorCode): Unit = {
    expect(Helpers.BAD_REQUEST) { Helpers.status(result) }
    expectHabitAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }

  // 401 Unauthorized
  protected def expectResultLoginRequired(result: Result): Unit = {
    expect(Helpers.UNAUTHORIZED) { Helpers.status(result) }
    expectHabitAsApi(result)

    expect(Some("OAuth")) { Helpers.header("WWW-Authenticate", result) }

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect("auth") { (json \ "result").asInstanceOf[JsString].value }
  }

  // 403 Forbidden
  protected def expectResultForbidden(result: Result): Unit = {
    expect(Helpers.FORBIDDEN) { Helpers.status(result) }
    expectHabitAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect("forbidden") { (json \ "result").asInstanceOf[JsString].value }
    expect(false) { StringUtils.isBlank((json \ "reason").asInstanceOf[JsString].value) }
  }

  // ----------------------------------------------------------------------
  // Result Exceptation (5xx)

  // 500 Internal Server Error
  protected def expectResultError(result: Result, ec: ServerErrorCode): Unit = {
    expect(Helpers.INTERNAL_SERVER_ERROR) { Helpers.status(result) }
    expectHabitAsApi(result)

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }

  // ----------------------------------------------------------------------
  // Shared

  private def expectHabitAsApi(result: Result): Unit = {
    expect(Some("application/json; charset=utf-8")) {
      Helpers.header(HeaderNames.CONTENT_TYPE, result)
    }
    expect(Some("no-cache")) {
      Helpers.header(HeaderNames.CACHE_CONTROL, result)
    }
  }
}
