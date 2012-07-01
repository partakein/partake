package in.partake.app;

import in.partake.app.impl.DebugPartakeAppInitializer;
import in.partake.app.impl.ReleasePartakeAppInitializer;

import org.apache.log4j.Logger;

import play.Application;
import play.GlobalSettings;

public class PartakeGlobalSettings extends GlobalSettings {
    private static final Logger logger = Logger.getLogger(PartakeGlobalSettings.class);
    private IPartakeAppInitializer initializer;
    boolean inTestMode;

    @Override
    public void beforeStart(Application app) {
        logger.info("PartakeGlobalSettings will start.");
        super.beforeStart(app);
    }

    @Override
    public void onStart(Application app) {
        logger.info("PartakeGlobalSettings is starting.");

        super.onStart(app);

        if (app.isTest())
            inTestMode = true;

        try {
            initializer = createInitializer(app);
            initializer.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(Application app) {
        logger.info("PartakeGlobalSettings is stopping.");

        try {
            initializer.cleanUp();
        } catch (Exception exceptionShouldBeIgnored) {
            logger.warn("Unintentional exception is thrown.", exceptionShouldBeIgnored);
        }

        super.onStop(app);
    }

    private IPartakeAppInitializer createInitializer(Application app) {
        if (app.isDev())
            return new DebugPartakeAppInitializer();

        if (app.isProd())
            return new ReleasePartakeAppInitializer();

        if (app.isTest()) {
            // We cannot get class from test unless test mode. So we use reflection here.
            try {
                Class<?> initializer = Class.forName("in.partake.app.impl.TestPartakeAppInitializer");
                return (IPartakeAppInitializer) initializer.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("class TestPartakeAppInitializer is not found", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("class TestPartakeAppInitializer cannot be instantiated", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class TestPartakeAppInitializer cannot be accessed", e);
            }
        }

        assert false;
        throw new RuntimeException("ASSERT NOT REACHED");
    }
}
