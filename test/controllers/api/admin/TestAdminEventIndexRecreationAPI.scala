package controllers.api.admin

import controllers.api.AbstractAPITest
import in.partake.resource.ServerErrorCode
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode
import in.partake.model.fixture.TestDataProviderConstants

class TestAdminEventIndexRecreationAPI extends AbstractAPITest {
  test("Accessing with admin user") {
    val request = FakeRequest("POST", "/api/admin/recreateEventIndex").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.ADMIN_USER_ID
    ).withFormUrlEncodedBody(
      Constants.Parameter.SESSION_TOKEN -> Constants.Parameter.VALID_SESSION_TOKEN_FOR_TEST
    )

    val result = AdminEventIndexRecreationAPI.run(request)
    expectResultOK(result)
  }

  test("Accessing with non-admin user") {
    val request = FakeRequest("POST", "/api/admin/recreateEventIndex").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    ).withFormUrlEncodedBody(
      Constants.Parameter.SESSION_TOKEN -> Constants.Parameter.VALID_SESSION_TOKEN_FOR_TEST
    )

    val result = AdminEventIndexRecreationAPI.run(request)
    expectResultForbidden(result)
  }

  test("Accessing without valid session token") {
    val request = FakeRequest("POST", "/api/admin/recreateEventIndex").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.ADMIN_USER_ID
    )

    val result = AdminEventIndexRecreationAPI.run(request)
    expectResultInvalid(result, UserErrorCode.INVALID_SECURITY_CSRF)
  }
}
