package pages.customer;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Deposit sub-panel on the customer dashboard.
 * Rendered inline (no URL change), so we stay on the same page object.
 */
public class DepositPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // ── Locators changed to @FindBy ──────────────────────────────────────────────
    @FindBy(css = "input[ng-model='amount']")
    private WebElement AMOUNT_INPUT;

    @FindBy(css = "button[type='submit']")
    private WebElement DEPOSIT_BTN;

    @FindBy(css = "span.error.ng-binding")
    private WebElement SUCCESS_MSG;


    public DepositPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Enter deposit amount: {amount}")
    public DepositPage enterAmount(String amount) {
        type(AMOUNT_INPUT, amount);
        return this;
    }

    @Step("Enter deposit amount: {amount}")
    public DepositPage enterAmount(int amount) {
        return enterAmount(String.valueOf(amount));
    }

    @Step("Click Deposit submit button")
    public DepositPage clickDeposit() {
        log.info("Clicking Deposit submit");
        click(DEPOSIT_BTN);
        return this;
    }

    /**
     * Enters amount and submits the deposit form.
     * @return this page (allows chaining / reading result message)
     */
    @Step("Deposit {amount}")
    public DepositPage deposit(int amount) {
        enterAmount(amount);
        clickDeposit();
        return this;
    }

    @Step("Deposit {amount}")
    public DepositPage deposit(String amount) {
        enterAmount(amount);
        clickDeposit();
        return this;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getResultMessage() {
        try {
            return getText(SUCCESS_MSG);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSuccessMessageVisible() {
        String msg = getResultMessage();
        return msg.toLowerCase().contains("deposit successful");
    }

    public boolean isAmountFieldVisible() {
        return isDisplayed(AMOUNT_INPUT);
    }

}
