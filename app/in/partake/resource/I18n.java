package in.partake.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class I18n {
    private static I18n instance = new I18n();
    
    private ResourceBundle jaBundle;
    private ResourceBundle enBundle;
    private ResourceBundle defaultBundle;
    private Map<Locale, ResourceBundle> bundles;
    
    public static I18n get() {
        return instance;
    }

    // TODO: なんらかの手法で Locale を取得する必要がある
    public static String t(String key) {
        return get().getBundle().getString(key);
    }

    private I18n() {
        this.bundles = new HashMap<Locale, ResourceBundle>();
        
        // resources_ja.properties & resources.properties will be read.
        this.jaBundle = ResourceBundle.getBundle("i18n.resource", Locale.JAPANESE, new UTF8Control()); 
        bundles.put(Locale.JAPANESE, jaBundle);
        
        // resources_en.properties & resources.properties will be read.
        this.enBundle = ResourceBundle.getBundle("i18n.resource", Locale.ENGLISH,  new UTF8Control());
        bundles.put(Locale.ENGLISH, enBundle);
        
        this.defaultBundle = this.jaBundle;
    } 
    
    // なんらかの手法で Locale を取得する必要がある
    public ResourceBundle getBundle() {
        return defaultBundle;
    }
    
    public ResourceBundle getBundle(Locale locale) {
        if (bundles.containsKey(locale)) {
            return bundles.get(locale);
        }
        
        return defaultBundle;
    }
}

class UTF8Control extends Control {
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        
        InputStream stream = getInputStream(loader, reload, resourceName); 
        if (stream == null)  { return null; }
        
        try {
            return new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
        } finally {
            stream.close();
        }
    }

    private InputStream getInputStream(ClassLoader loader, boolean reload, String resourceName) throws IOException {
        if (!reload) { return loader.getResourceAsStream(resourceName); }
        
        URL url = loader.getResource(resourceName);
        if (url == null) { return null; }
        
        URLConnection connection = url.openConnection();
        if (connection == null) { return null; }
        
        connection.setUseCaches(false);
        return connection.getInputStream();
    }
}
