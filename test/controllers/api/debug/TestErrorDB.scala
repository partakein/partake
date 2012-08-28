package controllers.api.debug

import controllers.api.AbstractAPITest
import in.partake.resource.ServerErrorCode
import play.api.test.FakeRequest

class TestErrorDB extends AbstractAPITest {
  test("accessing ErrorDB API") {
    val request = FakeRequest("GET", "/api/debug/errorDB")
    val result = ErrorDBAPI.run(request);

    expectResultError(result, ServerErrorCode.DB_ERROR)
  }
}

