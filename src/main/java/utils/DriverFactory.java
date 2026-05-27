package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Thread-safe WebDriver factory using ThreadLocal storage.
 * Each test thread gets its own isolated WebDriver instance,
 * enabling safe parallel test execution.
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {}

    /** Returns the WebDriver for the current thread, creating it if absent. */
    public static WebDriver getDriver() {
        if (driverHolder.get() == null) {
            driverHolder.set(createDriver());
        }
        return driverHolder.get();
    }

    /** Quits the driver for the current thread and clears the ThreadLocal. */
    public static void quitDriver() {
        WebDriver driver = driverHolder.get();
        if (driver != null) {
            driver.quit();
            driverHolder.remove();
            log.info("WebDriver closed for thread: {}", Thread.currentThread().getName());
        }
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private static WebDriver createDriver() {
        String browser  = config.getBrowser();
        boolean headless = config.isHeadless();
        log.info("Creating {} driver | headless={} | thread={}", browser, headless, Thread.currentThread().getName());

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefox(headless);
            case "edge"    -> createEdge(headless);
            default        -> createChrome(headless);
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // explicit waits only
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        if (headless) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments(
            "--disable-gpu",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080",
            "--disable-extensions",
            "--disable-infobars"
        );
        return new ChromeDriver(opts);
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions opts = new FirefoxOptions();
        if (headless) {
            opts.addArguments("--headless");
        }
        opts.addArguments("--width=1920", "--height=1080");
        return new FirefoxDriver(opts);
    }

    private static WebDriver createEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opts = new EdgeOptions();
        if (headless) {
            opts.addArguments("--headless=new");
        }
        opts.addArguments("--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        return new EdgeDriver(opts);
    }
}
