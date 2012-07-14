package in.partake.app;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

public class PartakeGlobalSettings extends GlobalSettings {
    @Override
    public void beforeStart(Application app) {
        Logger.info("PartakeGlobalSettings will start.");
        super.beforeStart(app);

        PartakeApp.setInstance(createPartakeApp(app));
    }

    @Override
    public void onStart(Application app) {
        Logger.info("PartakeGlobalSettings is starting.");

        super.onStart(app);

        try {
            PartakeApp.instance().createServices();
            PartakeApp.instance().initializeDBService();
            PartakeApp.instance().loadConfiguration(app.configuration());
            PartakeApp.instance().initializeOtherServices();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(Application app) {
        Logger.info("PartakeGlobalSettings is stopping.");

        try {
            PartakeApp.instance().cleanUp();
        } catch (Exception exceptionShouldBeIgnored) {
            Logger.warn("Unintentional exception is thrown.", exceptionShouldBeIgnored);
        }

        super.onStop(app);
    }

    @Override
    public Result onHandlerNotFound(RequestHeader arg0) {
        return Results.redirect("/notfound");
    }

    private PartakeApp createPartakeApp(Application app) {
        if (app.isDev() || app.isProd())
            return new PartakeApp();
        else {
            // We cannot get class from test unless test mode. So we use reflection here.
            try {
                Class<?> clazz = Class.forName("in.partake.app.PartakeTestApp");
                return (PartakeApp) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("class TestPartakeAppInitializer is not found", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("class TestPartakeAppInitializer cannot be instantiated", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("class TestPartakeAppInitializer cannot be accessed", e);
            }
        }
    }
}
