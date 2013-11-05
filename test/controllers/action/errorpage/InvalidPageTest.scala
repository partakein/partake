package controllers.action.errorpage
import org.junit.Test
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ServerErrorCode
import controllers.action.AbstractActionTest
import play.api.test.Helpers
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode

class InvalidPageTest extends AbstractActionTest {
  test("access without errorCode") {
    val request = FakeRequest("GET", "/invalid")
    implicit val context = InvalidPageAction.prepare(request)

    val params = InvalidPageAction.parseRequest(request)
    assertResult(None) { params.errorCode }

    val values = InvalidPageAction.executeAction(params)
    assertResult(None) { values.errorCode }

    val result = InvalidPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with errorCode") {
    val request = FakeRequest("GET", "/invalid?errorCode=" + UserErrorCode.INTENTIONAL_USER_ERROR.getErrorCode())
    implicit val context = InvalidPageAction.prepare(request)

    val params = InvalidPageAction.parseRequest(request)
    assertResult(Some(UserErrorCode.INTENTIONAL_USER_ERROR)) { params.errorCode }

    val values = InvalidPageAction.executeAction(params)
    assertResult(Some(UserErrorCode.INTENTIONAL_USER_ERROR)) { values.errorCode }

    val result = InvalidPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with login") {
    val request = FakeRequest("GET", "/invalid?errorCode=" + UserErrorCode.INTENTIONAL_USER_ERROR.getErrorCode()).withSession(
        Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    )
    implicit val context = InvalidPageAction.prepare(request)

    val params = InvalidPageAction.parseRequest(request)
    assertResult(Some(UserErrorCode.INTENTIONAL_USER_ERROR)) { params.errorCode }

    val values = InvalidPageAction.executeAction(params)
    assertResult(Some(UserErrorCode.INTENTIONAL_USER_ERROR)) { values.errorCode }

    val result = InvalidPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with invalid errorCode") {
    val request = FakeRequest("GET", "/invalid?errorCode=hogehoge")
    implicit val context = InvalidPageAction.prepare(request)

    val params = InvalidPageAction.parseRequest(request)
    assertResult(None) { params.errorCode }

    val values = InvalidPageAction.executeAction(params)
    assertResult(None) { values.errorCode }

    val result = InvalidPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }
}
