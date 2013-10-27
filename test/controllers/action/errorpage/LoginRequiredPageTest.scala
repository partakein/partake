package controllers.action.errorpage
import org.junit.Test
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ServerErrorCode
import controllers.action.AbstractActionTest
import play.api.test.Helpers
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode

class LoginRequiredPageTest extends AbstractActionTest {
  test("access without login") {
    val request = FakeRequest("GET", "/loginRequired")
    val result = LoginRequiredPageAction.run(request)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with login") {
    val request = FakeRequest("GET", "/loginRequired").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    )
    val result = LoginRequiredPageAction.run(request)
    assertResult(Helpers.SEE_OTHER) { Helpers.status(result) }
    assertResult(Some("/")) { Helpers.redirectLocation(result) }
  }
}
