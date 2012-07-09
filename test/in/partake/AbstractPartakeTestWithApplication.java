package in.partake;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import play.test.FakeApplication;
import play.test.Helpers;

/**
 * This is a base test case for PARTAKE with FakeApplication.
 */
public abstract class AbstractPartakeTestWithApplication {
    private static FakeApplication application;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("db.default.url", "jdbc:postgresql:partake-test");
        map.put("partake.lucene.indexdir", "/tmp/partake-lucene-test");

        application = Helpers.fakeApplication(map);
        Helpers.start(application);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
        Helpers.stop(application);
    }
}
