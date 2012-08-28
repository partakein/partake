package controllers.api.debug
import controllers.api.AbstractAPITest
import in.partake.resource.ServerErrorCode
import play.api.test.FakeRequest

class TestErrorAPI extends AbstractAPITest {
  test("accessing error API") {
    val request = FakeRequest("GET", "/api/debug/error")
    val result = ErrorAPI.run(request);

    expectResultError(result, ServerErrorCode.INTENTIONAL_ERROR)
  }
}
