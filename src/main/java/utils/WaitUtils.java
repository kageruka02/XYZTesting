package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Explicit wait helpers. All methods use the configured timeout from config.properties.
 * Never use Thread.sleep() — use these methods instead.
 */
public class WaitUtils {

    private static final Logger log = LoggerFactory.getLogger(WaitUtils.class);
    private static final int DEFAULT_TIMEOUT = ConfigReader.getInstance().getExplicitWait();

    private WaitUtils() {}

    private  static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    public  static WebDriverWait wait(WebDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ── Visibility ───────────────────────────────────────────────────────────

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        log.debug("Waiting for visibility: {}", locator);
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisible(WebDriver driver, WebElement element) {
        return wait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    public static List<WebElement> waitForAllVisible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    // ── Clickability ─────────────────────────────────────────────────────────

    public static WebElement waitForClickable(WebDriver driver, By locator) {
        log.debug("Waiting for clickability: {}", locator);
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(element));
    }

    // ── Presence ─────────────────────────────────────────────────────────────

    public static WebElement waitForPresence(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ── Text conditions ──────────────────────────────────────────────────────

    public static boolean waitForTextToBe(WebDriver driver, By locator, String expectedText) {
        return wait(driver).until(ExpectedConditions.textToBe(locator, expectedText));
    }

    public static boolean waitForTextContains(WebDriver driver, By locator, String partialText) {
        return wait(driver).until(ExpectedConditions.textToBePresentInElementLocated(locator, partialText));
    }

    // ── Alert handling ───────────────────────────────────────────────────────

    public static Alert waitForAlert(WebDriver driver) {
        return wait(driver).until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits for an alert, captures its text, then accepts it.
     * @return the alert message text
     */
    public static String acceptAlertAndGetText(WebDriver driver) {
        Alert alert = waitForAlert(driver);
        String text = alert.getText();
        log.info("Alert text: {}", text);
        alert.accept();
        return text;
    }

    // ── Invisibility ─────────────────────────────────────────────────────────

    public static boolean waitForInvisible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Custom condition ─────────────────────────────────────────────────────

    public static <T> T waitFor(WebDriver driver, ExpectedCondition<T> condition, int timeoutSeconds) {
        return wait(driver, timeoutSeconds).until(condition);
    }

    // ── URL / title ──────────────────────────────────────────────────────────

    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        return wait(driver).until(ExpectedConditions.urlContains(urlFragment));
    }

    // ── Element count ────────────────────────────────────────────────────────

    public static boolean waitForElementCount(WebDriver driver, By locator, int expectedCount) {
        return wait(driver).until(d -> d.findElements(locator).size() == expectedCount);
    }
}
