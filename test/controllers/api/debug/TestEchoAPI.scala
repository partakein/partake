package controllers.api.debug
import controllers.api.AbstractAPITest
import play.api.test.FakeRequest
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.test.Helpers
import play.api.libs.json.JsString
import in.partake.resource.UserErrorCode

class TestEchoAPI extends AbstractAPITest {
  test("accessing Echo API using get") {
    val request = FakeRequest("GET", "/api/debug/echo?data=test")
    val result = EchoAPI.run(request)

    expectResultOK(result)

    val json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult((json \ "data").asInstanceOf[JsString].value) { "test" }
  }

  test("accessing Echo API using get without argument") {
    val request = FakeRequest("GET", "/api/debug/echo")
    val result = EchoAPI.run(request)

    expectResultInvalid(result, UserErrorCode.INVALID_ARGUMENT)
  }

  test("accessing Echo API using post") {
    val request = FakeRequest("POST", "/api/debug/echo").withFormUrlEncodedBody(
        "data" -> "test"
    )
    val result = EchoAPI.run(request)

    expectResultOK(result)
    val json: JsValue = Json.parse(Helpers.contentAsString(result))
    assertResult((json \ "data").asInstanceOf[JsString].value) { "test" }
  }

  test("accessing Echo API using post without argument") {
    val request = FakeRequest("POST", "/api/debug/echo")
    val result = EchoAPI.run(request)

    expectResultInvalid(result, UserErrorCode.INVALID_ARGUMENT)
  }

  test("accessing Echo API using post with queryString") {
    val request = FakeRequest("POST", "/api/debug/echo?data=test")
    val result = EchoAPI.run(request)

    expectResultInvalid(result, UserErrorCode.INVALID_ARGUMENT)
  }

}

