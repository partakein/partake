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

class TestSuccessAPI extends AbstractAPITest {
  test("accessing success API") {
    val request = FakeRequest("GET", "/api/debug/success")
    val result = SuccessAPI.run(request);
    assertResult(Helpers.OK) { Helpers.status(result) }
  }
}
