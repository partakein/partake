package controllers.api.debug

import controllers.api.AbstractAPITest
import play.api.test.FakeRequest
import in.partake.resource.ServerErrorCode

class TestErrorDBException extends AbstractAPITest {
  test("accessing ErrorDBException API") {
    val request = FakeRequest("GET", "/api/debug/errorDBException")
    val result = ErrorDBExceptionAPI.run(request)

    expectResultError(result, ServerErrorCode.DB_ERROR)
  }
}
