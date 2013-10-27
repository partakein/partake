package controllers.api.admin

import controllers.api.AbstractAPITest
import in.partake.resource.ServerErrorCode
import play.api.test.FakeRequest
import in.partake.resource.Constants
import in.partake.resource.UserErrorCode
import in.partake.model.fixture.TestDataProviderConstants
import in.partake.resource.ConfigurationKeyConstants
import models.db.Transaction

class TestAdminModifySettingAPI extends AbstractAPITest {

  override def beforeEach() {
    super.beforeEach()
    Transaction { (con, daos) =>
      daos.getConfiguraitonItemAccess().remove(con, ConfigurationKeyConstants.KEY_FOR_TEST)
    }
  }

  test("Accessing with admin user") {
    val currentValue: Option[String] = loadAdminSetting(ConfigurationKeyConstants.KEY_FOR_TEST)
    assertResult(None) { currentValue }

    val request = FakeRequest("POST", "/api/admin/modifySetting").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.ADMIN_USER_ID
    ).withFormUrlEncodedBody(
      Constants.Parameter.SESSION_TOKEN -> Constants.Parameter.VALID_SESSION_TOKEN_FOR_TEST,
      "key" -> ConfigurationKeyConstants.KEY_FOR_TEST,
      "value" -> "value"
    )

    val result = AdminModifySettingAPI.run(request)
    expectResultOK(result)
  }

  test("Accessing with non-admin user") {
    val request = FakeRequest("POST", "/api/admin/modifySetting").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.DEFAULT_USER_ID
    ).withFormUrlEncodedBody(
      Constants.Parameter.SESSION_TOKEN -> Constants.Parameter.VALID_SESSION_TOKEN_FOR_TEST,
      "key" -> ConfigurationKeyConstants.KEY_FOR_TEST,
      "value" -> "value"
    )

    val result = AdminModifySettingAPI.run(request)
    expectResultForbidden(result)
  }

  test("Accessing without valid session token") {
    val request = FakeRequest("POST", "/api/admin/modifySetting").withSession(
      Constants.Session.USER_ID_KEY -> TestDataProviderConstants.ADMIN_USER_ID,
      "key" -> ConfigurationKeyConstants.KEY_FOR_TEST,
      "value" -> "value"
    )

    val result = AdminModifySettingAPI.run(request)
    expectResultInvalid(result, UserErrorCode.INVALID_SECURITY_CSRF)
  }
}
