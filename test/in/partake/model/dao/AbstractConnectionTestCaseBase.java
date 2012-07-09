package in.partake.model.dao;

import in.partake.AbstractPartakeTestWithApplication;
import in.partake.base.TimeUtil;

import org.junit.Test;

/**
 * @author shinyak
 *
 */
public abstract class AbstractConnectionTestCaseBase extends AbstractPartakeTestWithApplication {
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
