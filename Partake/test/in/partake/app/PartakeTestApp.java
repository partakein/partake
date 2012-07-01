package in.partake.app;

import in.partake.service.ITestService;

public class PartakeTestApp extends PartakeApp {
    protected static ITestService testService;

    public static ITestService getTestService() {
        return testService;
    }
}
