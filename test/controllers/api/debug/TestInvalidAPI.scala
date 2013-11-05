package controllers.api.debug

import controllers.api.AbstractAPITest
import org.junit.Test
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ServerErrorCode
import controllers.action.AbstractActionTest
import play.api.test.Helpers
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode
import play.api.libs.json.Json
import play.api.libs.json.JsValue

class TestInvalidAPI extends AbstractAPITest {
  test("accessing invalid API") {
    val request = FakeRequest("GET", "/api/debug/invalid")
    val result = InvalidAPI.run(request);
    assertResult(Helpers.BAD_REQUEST) { Helpers.status(result) }
    expectResultInvalid(result, UserErrorCode.INTENTIONAL_USER_ERROR)
  }
}
