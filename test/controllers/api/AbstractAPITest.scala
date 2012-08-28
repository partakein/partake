package controllers.api

import controllers.AbstractControllerTest
import in.partake.resource.UserErrorCode
import in.partake.resource.ServerErrorCode
import play.api.test.Helpers
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.libs.json.JsString

abstract class AbstractAPITest extends AbstractControllerTest {

  // Result Expectation (2xx)
  def expectResultOK(result: Result): Unit = {
    expect(Helpers.OK) { Helpers.status(result) }
  }

  // Result Exceptation (4xx)

  // 400 Bad Request
  def expectResultInvalid(result: Result, ec: UserErrorCode): Unit = {
    expect(Helpers.BAD_REQUEST) { Helpers.status(result) }

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }

  // 401 Unauthorized
  def expectResultLoginRequired(result: Result): Unit = {
    expect(Helpers.UNAUTHORIZED) { Helpers.status(result) }

    expect(Some("OAuth")) { Helpers.header("WWW-Authenticate", result) }

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect("auth") { (json \ "result").asInstanceOf[JsString].value }
  }

  // Result Exceptation (5xx)
  def expectResultError(result: Result, ec: ServerErrorCode): Unit = {
    expect(Helpers.INTERNAL_SERVER_ERROR) { Helpers.status(result) }

    var json: JsValue = Json.parse(Helpers.contentAsString(result))
    expect(ec.getReasonString()) { (json \ "reason").asInstanceOf[JsString].value }
  }
}
