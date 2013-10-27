package controllers.action.errorpage
import org.junit.Test
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ServerErrorCode
import controllers.action.AbstractActionTest
import play.api.test.Helpers
import play.api.test.FakeRequest
import in.partake.resource.Constants

class ErrorPageTest extends AbstractActionTest {
  test("access without errorCode") {
    val request = FakeRequest("GET", "/error")
    implicit val context = ErrorPageAction.prepare(request)

    val params = ErrorPageAction.parseRequest(request)
    assertResult(None) { params.errorCode }

    val values = ErrorPageAction.executeAction(params)
    assertResult(None) { values.errorCode }

    val result = ErrorPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with errorCode") {
    val request = FakeRequest("GET", "/error?errorCode=" + ServerErrorCode.INTENTIONAL_ERROR.getErrorCode())
    implicit val context = ErrorPageAction.prepare(request)

    val params = ErrorPageAction.parseRequest(request)
    assertResult(Some(ServerErrorCode.INTENTIONAL_ERROR)) { params.errorCode }

    val values = ErrorPageAction.executeAction(params)
    assertResult(Some(ServerErrorCode.INTENTIONAL_ERROR)) { values.errorCode }

    val result = ErrorPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with login") {
    val request = FakeRequest("GET", "/error?errorCode=" + ServerErrorCode.INTENTIONAL_ERROR.getErrorCode()).withSession(
        Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    )
    implicit val context = ErrorPageAction.prepare(request)

    val params = ErrorPageAction.parseRequest(request)
    assertResult(Some(ServerErrorCode.INTENTIONAL_ERROR)) { params.errorCode }

    val values = ErrorPageAction.executeAction(params)
    assertResult(Some(ServerErrorCode.INTENTIONAL_ERROR)) { values.errorCode }

    val result = ErrorPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }

  test("access with invalid errorCode") {
    val request = FakeRequest("GET", "/error?errorCode=hogehoge")
    implicit val context = ErrorPageAction.prepare(request)

    val params = ErrorPageAction.parseRequest(request)
    assertResult(None) { params.errorCode }

    val values = ErrorPageAction.executeAction(params)
    assertResult(None) { values.errorCode }

    val result = ErrorPageAction.renderResult(values)
    assertResult(Helpers.OK) { Helpers.status(result) }
  }
}
