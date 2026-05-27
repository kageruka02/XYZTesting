package pages.manager;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;

/**
 * Manager dashboard page — contains tabs for Add Customer, Open Account, and Customers list.
 * URL: #/manager
 *
 * Each tab click is followed by an explicit wait for the tab's own content to become
 * visible; AngularJS uses ng-show to toggle panels and the digest cycle introduces
 * a small lag that a simple click() is not enough to bridge.
 */
public class ManagerPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By ADD_CUSTOMER_TAB  = By.cssSelector("button[ng-click='addCust()']");
    private static final By OPEN_ACCOUNT_TAB  = By.cssSelector("button[ng-click='openAccount()']");
    private static final By CUSTOMERS_TAB     = By.cssSelector("button[ng-click='showCust()']");
    private static final By HOME_BTN          = By.cssSelector("button[ng-click='home()']");

    // Content sentinels — each tab's first visible element used as a readiness signal
    private static final By ADD_CUST_SENTINEL   = By.cssSelector("input[placeholder='First Name']");
    private static final By OPEN_ACCT_SENTINEL  = By.id("userSelect");
    private static final By CUSTOMERS_SENTINEL  = By.cssSelector("table.table");

    public ManagerPage(WebDriver driver) {
        super(driver);
    }

    // ── Tab navigation ────────────────────────────────────────────────────────

    @Step("Open 'Add Customer' tab")
    public AddCustomerPage goToAddCustomer() {
        log.info("Navigating to Add Customer tab");
        click(ADD_CUSTOMER_TAB);
        // Wait for the form to be rendered by AngularJS before handing off
        WaitUtils.waitForVisible(driver, ADD_CUST_SENTINEL);
        return new AddCustomerPage(driver);
    }

    @Step("Open 'Open Account' tab")
    public OpenAccountPage goToOpenAccount() {
        log.info("Navigating to Open Account tab");
        click(OPEN_ACCOUNT_TAB);
        // Wait for the customer dropdown to appear (AngularJS ng-show)
        WaitUtils.waitForVisible(driver, OPEN_ACCT_SENTINEL);
        return new OpenAccountPage(driver);
    }

    @Step("Open 'Customers' tab")
    public CustomersPage goToCustomers() {
        log.info("Navigating to Customers tab");
        click(CUSTOMERS_TAB);
        // Wait for the table to be visible AND for ng-repeat to render at least one row
        WaitUtils.waitForVisible(driver, CUSTOMERS_SENTINEL);
        WaitUtils.waitFor(driver,
            d -> !d.findElements(By.cssSelector("table.table tbody tr")).isEmpty(),
            15);
        return new CustomersPage(driver);
    }

    @Step("Click Home")
    public void clickHome() {
        click(HOME_BTN);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isOnManagerPage() {
        return getCurrentUrl().contains("manager");
    }
}
