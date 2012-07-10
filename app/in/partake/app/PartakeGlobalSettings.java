package in.partake.app;

import in.partake.app.impl.DebugPartakeAppInitializer;
import in.partake.app.impl.ReleasePartakeAppInitializer;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

public class PartakeGlobalSettings extends GlobalSettings {
    private IPartakeAppInitializer initializer;

    @Override
    public void beforeStart(Application app) {
        Logger.info("PartakeGlobalSettings will start.");
        super.beforeStart(app);

        PartakeConfiguration.set(app.configuration());
    }

    @Override
    public void onStart(Application app) {
        Logger.info("PartakeGlobalSettings is starting.");

        super.onStart(app);

        try {
            initializer = createInitializer(app);
            initializer.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(Application app) {
        Logger.info("PartakeGlobalSettings is stopping.");

        try {
            if (initializer != null)
                initializer.cleanUp();
        } catch (Exception exceptionShouldBeIgnored) {
            Logger.warn("Unintentional exception is thrown.", exceptionShouldBeIgnored);
        }

        super.onStop(app);
    }

    @Override
    public Result onHandlerNotFound(RequestHeader arg0) {
        return Results.redirect("/notfound");
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
