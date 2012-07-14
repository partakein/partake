package in.partake.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ConfigurationKeyConstants {
    public static final String TOPPATH = "partake.toppath";
    public static final String DAEMON_ENABLED = "partake.twitterdaemon.enabled";
    public static final String TWITTER_BOT_ID = "partake.twitterbot.id";
    public static final String LUCENE_INDEX_DIX = "partake.lucene.indexdir";
    public static final String GOOGLE_ANALYTICS_ID = "partake.analytics.google";
    public static final String TWITTER4J_CONSUMER_KEY = "twitter4j.oauth.consumerKey";
    public static final String TWITTER4J_CONSUMER_SECRET = "twitter4j.oauth.consumerSecret";
    public static final String ADMIN_SCREEN_NAMES = "partake.admin.screennames";

    public static final Set<String> configurationkeySet = new HashSet<String>(Arrays.asList(
            TOPPATH,
            DAEMON_ENABLED,
            TWITTER_BOT_ID,
            LUCENE_INDEX_DIX,
            GOOGLE_ANALYTICS_ID,
            TWITTER4J_CONSUMER_KEY,
            TWITTER4J_CONSUMER_SECRET,
            ADMIN_SCREEN_NAMES
    ));
}
