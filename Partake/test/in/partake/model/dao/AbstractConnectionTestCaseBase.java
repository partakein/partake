package in.partake.model.dao;

import in.partake.base.TimeUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.FakeApplication;
import play.test.Helpers;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase {
    private static FakeApplication application;

    @BeforeClass
    public static void setUpOnce() throws Exception {
    	System.out.println("********** START!");
        application = Helpers.fakeApplication();
        Helpers.start(application);
    }

    @AfterClass
    public static void tearDownOnce() throws Exception {
    	System.out.println("********** STOP!");
        Helpers.stop(application);
    }

    // ------------------------------------------------------------

    protected void setup() throws Exception {
        // remove the current data
        TimeUtil.resetCurrentDate();
    }

    // ------------------------------------------------------------

    @Test
    public final void shouldAlwaysSucceed() {
        // do nothing
        // NOTE: this method ensures the setup method is called when no other test methods are defined.
    }
}
