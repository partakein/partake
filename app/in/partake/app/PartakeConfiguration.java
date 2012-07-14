package in.partake.app;

import in.partake.base.PartakeException;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IConfigurationItemAccess;
import in.partake.model.dto.ConfigurationItem;
import in.partake.resource.ConfigurationKeyConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import play.Configuration;
import play.Logger;

public class PartakeConfiguration {
    private static Configuration configuration;

    private static String toppath;
    private static String daemonEnabled;
    private static String twitterBotId;
    private static String luceneIndexDir;
    private static String googleAnalyticsId;
    private static String twitter4jConsumerKey;
    private static String twitter4jConsumerSecret;
    private static String adminScreenNames;
    private static Set<String> adminScreenNamesParsed;

    public static void set(Configuration conf) {
        // NOTE: set() will be called after loadFromDB(). So configuration file will override the items from DB.
        configuration = conf;

        if (configuration.getString(ConfigurationKeyConstants.TOPPATH) != null)
            toppath = configuration.getString(ConfigurationKeyConstants.TOPPATH);
        if (configuration.getString(ConfigurationKeyConstants.DAEMON_ENABLED) != null)
            daemonEnabled = configuration.getString(ConfigurationKeyConstants.DAEMON_ENABLED);
        if (configuration.getString(ConfigurationKeyConstants.TWITTER_BOT_ID) != null)
            twitterBotId = configuration.getString(ConfigurationKeyConstants.TWITTER_BOT_ID);
        if (configuration.getString(ConfigurationKeyConstants.LUCENE_INDEX_DIX) != null)
            luceneIndexDir = configuration.getString(ConfigurationKeyConstants.LUCENE_INDEX_DIX);
        if (configuration.getString(ConfigurationKeyConstants.GOOGLE_ANALYTICS_ID) != null)
            googleAnalyticsId = configuration.getString(ConfigurationKeyConstants.GOOGLE_ANALYTICS_ID);
        if (configuration.getString(ConfigurationKeyConstants.TWITTER4J_CONSUMER_KEY) != null)
            twitter4jConsumerKey = configuration.getString(ConfigurationKeyConstants.TWITTER4J_CONSUMER_KEY);
        if (configuration.getString(ConfigurationKeyConstants.TWITTER4J_CONSUMER_SECRET) != null)
            twitter4jConsumerSecret = configuration.getString(ConfigurationKeyConstants.TWITTER4J_CONSUMER_SECRET);
        if (configuration.getString(ConfigurationKeyConstants.ADMIN_SCREEN_NAMES) != null)
            adminScreenNames = configuration.getString(ConfigurationKeyConstants.ADMIN_SCREEN_NAMES);

        if (adminScreenNames != null)
            adminScreenNamesParsed = parseAdministratorScreenNames(adminScreenNames);
    }

    public static void loadFromDB() throws DAOException, PartakeException {
        new DBAccess<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                IConfigurationItemAccess dao = daos.getConfiguraitonItemAccess();

                toppath = getValue(dao.find(con, ConfigurationKeyConstants.TOPPATH));
                daemonEnabled = getValue(dao.find(con, ConfigurationKeyConstants.DAEMON_ENABLED));
                twitterBotId = getValue(dao.find(con, ConfigurationKeyConstants.TWITTER_BOT_ID));
                luceneIndexDir = getValue(dao.find(con, ConfigurationKeyConstants.LUCENE_INDEX_DIX));
                googleAnalyticsId = getValue(dao.find(con, ConfigurationKeyConstants.GOOGLE_ANALYTICS_ID));
                twitter4jConsumerKey = getValue(dao.find(con, ConfigurationKeyConstants.TWITTER4J_CONSUMER_KEY));
                twitter4jConsumerSecret = getValue(dao.find(con, ConfigurationKeyConstants.TWITTER4J_CONSUMER_SECRET));
                adminScreenNames = getValue(dao.find(con, ConfigurationKeyConstants.ADMIN_SCREEN_NAMES));

                return null;
            }
        }.execute();
    }

    private static String getValue(ConfigurationItem item) {
        if (item == null)
            return null;
        return item.value();
    }

    public static String toppath() {
        return toppath;
    }

    public static boolean isTwitterDaemonEnabled() {
        return "true".equals(daemonEnabled);
    }

    public static long twitterBotId() {
        try {
            return Long.parseLong(twitterBotId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String luneceIndexDir() {
        return luceneIndexDir;
    }

    public static Set<String> administratorScreenNames() {
        return adminScreenNamesParsed;
    }

    public static String googleAnalyticsId() {
        return googleAnalyticsId;
    }

    public static String twitter4jConsumerKey() {
        return twitter4jConsumerKey;
    }

    public static String twitter4jConsumerSecret() {
        return twitter4jConsumerSecret;
    }

    private static Set<String> parseAdministratorScreenNames(String adminNames) {
        if (adminNames == null)
            return Collections.emptySet();

        Set<String> result = new HashSet<String>();
        for (String name : adminNames.split(",")) {
            String adminName = name.trim().toLowerCase();
            if (!adminName.isEmpty())
                result.add(adminName);
        }

        if (result.isEmpty())
            Logger.warn("No administrator screen names");

        return result;
    }
}
