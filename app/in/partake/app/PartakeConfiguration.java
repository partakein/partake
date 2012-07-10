package in.partake.app;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import play.Logger;

import play.Configuration;

public class PartakeConfiguration {
    private static Configuration configuration;
    private static String toppath;
    private static Set<String> administratorScreenNames;

    public static void set(Configuration conf) {
        configuration = conf;

        toppath = configuration.getString("partake.toppath");
        administratorScreenNames = parseAdministratorScreenNames();
    }

    public static String toppath() {
        return toppath;
    }

    public static boolean isTwitterDaemonEnabled() {
        String str = configuration.getString("partake.twitterdaemon.disabled");

        if (str == null)
            return true;

        if ("true".equals(str))
            return false;

        return true;
    }

    public static long twitterBotId() {
        String idStr = configuration.getString("partake.twitterbot.id");
        if (idStr == null)
            return -1;

        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String luneceIndexDir() {
        return configuration.getString("partake.lucene.indexdir");
    }

    public static Set<String> administratorScreenNames() {
        return administratorScreenNames;
    }

    public static String googleAnalyticsId() {
        return configuration.getString("partake.analytics.google");
    }

    private static Set<String> parseAdministratorScreenNames() {
        String adminNames = configuration.getString("partake.admin.screennames");
        if (adminNames == null) {
            Logger.warn("partake.admin.screennames is not found in the current configuration file");
            return Collections.emptySet();
        }

        Set<String> result = new HashSet<String>();
        for (String name : adminNames.split(",")) {
            String adminName = name.trim().toLowerCase();
            if (!adminName.isEmpty())
                result.add(adminName);
        }

        if (result.isEmpty())
            Logger.warn("partake.admin.screennames is empty");

        return result;
    }
}
