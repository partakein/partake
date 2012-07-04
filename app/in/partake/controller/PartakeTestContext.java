package in.partake.controller;

import in.partake.controller.base.AbstractPartakeController;

// TODO(mayah): Actually this class is used for test, however we should expose this class
// to non testing environment. See also ActionProxy.getAction() also.
public final class PartakeTestContext {
    // private static ThreadLocal<AbstractPartakeController> action = new ThreadLocal<AbstractPartakeController>();
    private static AbstractPartakeController action;

    public static void setAction(AbstractPartakeController action) {
        // PartakeTestContext.action.set(action);
        PartakeTestContext.action = action;
    }

    public static AbstractPartakeController getAction() {
        // return action.get();
        return action;
    }
}
