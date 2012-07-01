package in.partake.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/** Partake の動作に必要なデータを、partake.properties, 及び {mode}.partake.properties から読み出す。*/
public class PartakeProperties {
    private static final Logger logger = Logger.getLogger(PartakeProperties.class);
    private static final PartakeProperties instance = new PartakeProperties();
    private static final String PROP_ADMIN_NAMES = "in.partake.twitter.admin";
    private String mode;
    private Properties properties;
    private Set<String> administratorNames;

    public static PartakeProperties get() {
        return instance;
    }

    private PartakeProperties() {
    }

    /** mode 名を用いて読みなおす。初期化及びユニットテスト用途。 */
    public void reset(String mode) {
        logger.info("Loading " + mode + ".partake.properties...");
        this.mode = mode;
        this.properties = readFrom("/" + mode + ".partake.properties");
        this.administratorNames = Collections.unmodifiableSet(parseAdministratorNames());
    }

    /** mode 名を fetch してから読みなおす。初期化及びユニットテスト用途。 */
    public void reset() {
        reset(fetchMode());
    }

    // ----------------------------------------------------------------------
    // general accessor

    public String getString(String name) {
        return properties.getProperty(name);
    }

    public int getInt(String name) {
        return Integer.parseInt(properties.getProperty(name));
    }

    public boolean getBoolean(String name) {
        String value = properties.getProperty(name);
        if (value == null)
            return false;
        return Boolean.parseBoolean(value);
    }

    // ----------------------------------------------------------------------

    public String getMode() {
        return this.mode;
    }

    public Set<String> getTwitterAdminNames() {
        return this.administratorNames;
    }

    public String getLuceneIndexDirectory() {
        return properties.getProperty("in.partake.lucene.indexdir");
    }

    public String getPartakeAppFactoryClassName() {
        String str = properties.getProperty("in.partake.app.factory");
        if (str == null)
            str = "in.partake.app.imple.DefaultPartakeAppFactory";

        return str;
    }

    public String getTopPath() {
        return properties.getProperty("in.partake.toppath");
    }

    public String getGoogleAnalyticsCode() {
        return properties.getProperty("in.partake.analytics.google");
    }

    public boolean isEnabledTwitterDaemon() {
        String str = properties.getProperty("in.partake.twitterdaemon.disabled");
        if (str == null) { return true; }
        if ("true".equals(str)) { return false; }
        return true;
    }

    // ----------------------------------------------------------------------
    // Twitter Bot

    public long getTwitterBotTwitterId() {
        try {
            return Long.parseLong(properties.getProperty("in.partake.twitterbot.twitterid"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ----------------------------------------------------------------------
    // JPA Connection properties

    public String getJPAPersistenceUnitName() {
        return properties.getProperty("in.partake.database.jpa.persistenceunit");
    }

    // --------------------------------------------------

    /** read partake.properties and load.*/
    private String fetchMode() {
        Properties properties = readFrom("/partake.properties");
        if (properties == null) {
            throw new RuntimeException("partake.properties does not exist.");
        }

        return properties.getProperty("in.partake.mode");
    }

    private Properties readFrom(String resourceName) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream(resourceName);
            properties.load(inputStream);

            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private Set<String> parseAdministratorNames() {
        String adminNames = properties.getProperty(PROP_ADMIN_NAMES);
        Set<String> result = new HashSet<String>();
        if (adminNames == null) {
            logger.warn(PROP_ADMIN_NAMES + " is not found in current property file.");
            return result;
        }

        for (String name : adminNames.split(",")) {
            String adminName = name.trim().toLowerCase();
            if (!adminName.isEmpty()) {
                result.add(adminName);
            }
        }
        if (result.isEmpty()) {
            logger.warn(PROP_ADMIN_NAMES + " in current property file is empty.");
        }
        return result;
    }

}
