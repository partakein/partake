package controllers.api.debug

import controllers.api.AbstractAPITest
import play.api.test.FakeRequest
import play.api.test.Helpers
import in.partake.resource.Constants
import in.partake.model.fixture.TestDataProviderConstants

class TestSuccessIfLogin extends AbstractAPITest {
  test("accessing SuccessIfLogin API with login") {
    val request = FakeRequest("GET", "/api/debug/successIfLogin").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    )
    val result = SuccessIfLoginAPI.run(request);

    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("accessing SuccessIfLogin API without login") {
    val request = FakeRequest("GET", "/api/debug/successIfLogin")
    val result = SuccessIfLoginAPI.run(request);

    expectResultLoginRequired(result);
  }
}
