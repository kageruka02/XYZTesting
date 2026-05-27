package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton utility that loads config.properties and exposes typed accessors.
 * System properties (e.g. -Dbrowser=firefox) always override file values,
 * which allows CI pipelines to inject settings without modifying the file.
 */
public class ConfigReader {

    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static ConfigReader instance;
    private final Properties props = new Properties();

    private ConfigReader() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException("Cannot find " + CONFIG_FILE + " on the classpath");
            }
            props.load(in);
            log.info("Loaded configuration from {}", CONFIG_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE, e);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Returns the property value; system properties take precedence over the file.
     */
    public String get(String key) {
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.isBlank()) {
            return systemProp.trim();
        }
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in " + CONFIG_FILE);
        }
        return value.trim();
    }

    public String getBaseUrl()          { return get("base.url"); }
    public String getBrowser()          { return get("browser").toLowerCase(); }
    public boolean isHeadless()         { return Boolean.parseBoolean(get("headless")); }
    public int getExplicitWait()        { return Integer.parseInt(get("explicit.wait")); }
    public int getPageLoadTimeout()     { return Integer.parseInt(get("page.load.timeout")); }
    public boolean screenshotOnFailure(){ return Boolean.parseBoolean(get("screenshot.on.failure")); }
    public String getScreenshotDir()    { return get("screenshot.dir"); }
}
