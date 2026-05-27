package base;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.HomePage;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.ScreenshotUtils;

import java.lang.reflect.Method;

/**
 * Base class for all test classes.
 *
 * Lifecycle:
 *  @BeforeMethod — creates a fresh WebDriver, opens the app URL
 *  @AfterMethod  — captures screenshot on failure, quits the driver
 *
 * Thread-safe: each test thread owns its own WebDriver via ThreadLocal (DriverFactory).
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected static final ConfigReader config = ConfigReader.getInstance();

    // ── Setup ────────────────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method testMethod) {
        log.info("=== Starting test: {} ===", testMethod.getName());
        WebDriver driver = DriverFactory.getDriver();
        driver.get(config.getBaseUrl());
        log.info("Navigated to: {}", config.getBaseUrl());
    }

    // ── Teardown ─────────────────────────────────────────────────────────────

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {}", testName);
            if (config.screenshotOnFailure()) {
                ScreenshotUtils.captureAndAttach(DriverFactory.getDriver(), testName);
            }
            // Attach failure reason to Allure
            Throwable cause = result.getThrowable();
            if (cause != null) {
                Allure.addAttachment("Failure reason", cause.getMessage());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("TEST PASSED: {}", testName);
        } else {
            log.warn("TEST SKIPPED: {}", testName);
        }

        DriverFactory.quitDriver();
    }

    // ── Convenience accessor ─────────────────────────────────────────────────

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /** Opens the application home page and returns it. */
    protected HomePage openHomePage() {
        getDriver().get(config.getBaseUrl());
        return new HomePage(getDriver());
    }
}
