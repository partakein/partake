package controllers.action.errorpage
import org.junit.Test
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ServerErrorCode
import controllers.action.AbstractActionTest
import play.api.test.Helpers
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode

class NotFoundPageTest extends AbstractActionTest {
  test("access without login") {
    val request = FakeRequest("GET", "/notfound")
    val result = NotFoundPageAction.run(request)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with login") {
    val request = FakeRequest("GET", "/notfound").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    )
    val result = NotFoundPageAction.run(request)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }
}
