package pages;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.customer.CustomerLoginPage;
import pages.manager.ManagerPage;

/**
 * Represents the XYZ Bank home/landing page.
 * URL: https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login
 */
public class HomePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By MANAGER_LOGIN_BTN = By.cssSelector("button[ng-click='manager()']");
    private static final By CUSTOMER_LOGIN_BTN = By.cssSelector("button[ng-click='customer()']");
    private static final By PAGE_HEADING = By.cssSelector("div.home h1");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Click 'Bank Manager Login'")
    public ManagerPage clickBankManagerLogin() {
        log.info("Clicking Bank Manager Login");
        click(MANAGER_LOGIN_BTN);
        waitForUrlContains("manager");
        return new ManagerPage(driver);
    }

    @Step("Click 'Customer Login'")
    public CustomerLoginPage clickCustomerLogin() {
        log.info("Clicking Customer Login");
        click(CUSTOMER_LOGIN_BTN);
        waitForUrlContains("customer");
        return new CustomerLoginPage(driver);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isManagerLoginButtonVisible() {
        return isDisplayed(MANAGER_LOGIN_BTN);
    }

    public boolean isCustomerLoginButtonVisible() {
        return isDisplayed(CUSTOMER_LOGIN_BTN);
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING);
    }
}
