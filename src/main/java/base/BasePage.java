package base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

/**
 * Base class for all Page Objects.
 * Provides common WebDriver interactions with built-in explicit waits.
 * All page classes extend this to avoid duplicating low-level driver calls.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    // ── Element interactions ─────────────────────────────────────────────────

    /** Clicks an element after waiting for it to be clickable. */
    protected void click(By locator) {
        log.debug("Clicking: {}", locator);
        WaitUtils.waitForClickable(driver, locator).click();
    }

    protected void click(WebElement element) {
        WaitUtils.waitForClickable(driver, element).click();
    }

    /** Clears any existing text, then types the given value. */
    protected void type(By locator, String text) {
        log.debug("Typing '{}' into: {}", text, locator);
        WebElement el = WaitUtils.waitForVisible(driver, locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void type(WebElement element, String text) {
        WaitUtils.waitForVisible(driver, element).clear();
        element.sendKeys(text);
    }

    /** Returns the trimmed visible text of an element. */
    protected String getText(By locator) {
        return WaitUtils.waitForVisible(driver, locator).getText().trim();
    }

    protected String getText(WebElement element) {
        return WaitUtils.waitForVisible(driver, element).getText().trim();
    }

    protected String getAttribute(By locator, String attribute) {
        return WaitUtils.waitForPresence(driver, locator).getAttribute(attribute);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }


    // ── Dropdown helpers ─────────────────────────────────────────────────────

    protected void selectByVisibleText(By locator, String text) {
        log.debug("Selecting '{}' from: {}", text, locator);
        new Select(WaitUtils.waitForVisible(driver, locator)).selectByVisibleText(text);
    }

    protected void selectByVisibleText(WebElement element, String text) {
        new Select(WaitUtils.waitForVisible(driver, element)).selectByVisibleText(text);
    }

    protected String getSelectedOption(By locator) {
        return new Select(WaitUtils.waitForVisible(driver, locator)).getFirstSelectedOption().getText().trim();
    }

    protected String getSelectedOption(WebElement element) {
        return new Select(WaitUtils.waitForVisible(driver, element))
                .getFirstSelectedOption()
                .getText()
                .trim();
    }


    // ── Wait shortcuts ───────────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        return WaitUtils.waitForVisible(driver, locator);
    }

    protected void waitForUrlContains(String fragment) {
        WaitUtils.waitForUrlContains(driver, fragment);
    }

    public String acceptAlert() {
        return WaitUtils.acceptAlertAndGetText(driver);
    }

    // ── JavaScript helpers ────────────────────────────────────────────────────

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}
