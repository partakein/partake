package controllers.api.debug
import controllers.api.AbstractAPITest
import play.api.test.FakeRequest
import in.partake.resource.ServerErrorCode

class TestErrorException extends AbstractAPITest {
  test("accessing invalid API") {
    val request = FakeRequest("GET", "/api/debug/errorException")
    val result = ErrorExceptionAPI.run(request)

    expectResultError(result, ServerErrorCode.UNKNOWN_ERROR)
  }
}
