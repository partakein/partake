package controllers

import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import in.partake.app.PartakeTestApp
import in.partake.base.TimeUtil
import in.partake.controller.ActionProxy
import in.partake.resource.Constants
import play.api.test.FakeApplication
import play.api.test.Helpers
import play.api.Play
import org.scalatest.FunSuite

abstract class AbstractControllerTest extends FunSuite with BeforeAndAfterEach with BeforeAndAfterAll {

  override def beforeAll() {
    val map = Map(
      "db.default.url" -> "jdbc:postgresql:partake-test",
      "partake.lucene.indexdir" -> "/tmp/partake-lucene-test"
    )

    val fakeApp: FakeApplication = FakeApplication(additionalConfiguration = map)
    Play.start(fakeApp)
  }

  override def afterAll() {
    Play.stop()
  }

  override def beforeEach() {
    PartakeTestApp.getTestService().setDefaultFixtures()
    TimeUtil.resetCurrentDate()
  }

  override def afterEach() {
  }
}
