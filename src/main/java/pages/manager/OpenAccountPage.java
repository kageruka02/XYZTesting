package pages.manager;

import base.BasePage;
import io.qameta.allure.Step;
import models.AccountData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utils.WaitUtils;

/**
 * "Open Account" form within the Manager section.
 * Allows creating a bank account for an existing customer with a chosen currency.
 *
 * AngularJS note: the customer dropdown uses ng-options. We wait for it to be
 * populated (options > 1) before selecting, otherwise the selection fires before
 * Angular has rendered the option elements.
 */
public class OpenAccountPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By CUSTOMER_SELECT  = By.id("userSelect");
    private static final By CURRENCY_SELECT  = By.id("currency");
    private static final By PROCESS_BTN      = By.cssSelector("button[type='submit']");

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Select customer: {customerName}")
    public OpenAccountPage selectCustomer(String customerName) {
        log.info("Selecting customer: {}", customerName);
        // Wait for ng-options to bind before attempting selection
        WaitUtils.waitFor(driver, d -> {
            try {
                return new Select(d.findElement(CUSTOMER_SELECT)).getOptions().size() > 1;
            } catch (Exception e) { return false; }
        }, 15);
        selectByVisibleText(CUSTOMER_SELECT, customerName);
        return this;
    }

    @Step("Select currency: {currency}")
    public OpenAccountPage selectCurrency(String currency) {
        log.info("Selecting currency: {}", currency);
        selectByVisibleText(CURRENCY_SELECT, currency);
        return this;
    }

    @Step("Click 'Process' to create account")
    public OpenAccountPage clickProcess() {
        log.info("Clicking Process button");
        click(PROCESS_BTN);
        return this;
    }

    /**
     * Fills the form, clicks Process, and accepts the resulting alert.
     * @return the alert text (contains the new account number)
     */
    @Step("Open account for customer: {account.customerName} with currency: {account.currency}")
    public String openAccount(AccountData account) {
        selectCustomer(account.getCustomerName());
        selectCurrency(account.getCurrency());
        clickProcess();
        return acceptAlert();
    }

    /** Overload for direct string args. */
    @Step("Open account for {customerName} / {currency}")
    public String openAccount(String customerName, String currency) {
        selectCustomer(customerName);
        selectCurrency(currency);
        clickProcess();
        return acceptAlert();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getSelectedCustomer() {
        return getSelectedOption(CUSTOMER_SELECT);
    }

    public String getSelectedCurrency() {
        return getSelectedOption(CURRENCY_SELECT);
    }

    public boolean isFormVisible() {
        return isDisplayed(CUSTOMER_SELECT);
    }
}
