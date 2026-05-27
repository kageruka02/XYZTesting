package pages.customer;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Withdraw sub-panel on the customer dashboard.
 * Rendered inline (no URL change).
 */
public class WithdrawPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By AMOUNT_INPUT   = By.cssSelector("input[ng-model='amount']");
    private static final By WITHDRAW_BTN   = By.cssSelector("button[type='submit']");
    private static final By RESULT_MSG     = By.cssSelector("span.error.ng-binding");

    public WithdrawPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Enter withdrawal amount: {amount}")
    public WithdrawPage enterAmount(String amount) {
        type(AMOUNT_INPUT, amount);
        return this;
    }

    @Step("Enter withdrawal amount: {amount}")
    public WithdrawPage enterAmount(int amount) {
        return enterAmount(String.valueOf(amount));
    }

    @Step("Click Withdraw submit button")
    public WithdrawPage clickWithdraw() {
        log.info("Clicking Withdraw submit");
        click(WITHDRAW_BTN);
        return this;
    }

    /**
     * Enters amount and submits the withdraw form.
     */
    @Step("Withdraw {amount}")
    public WithdrawPage withdraw(int amount) {
        enterAmount(amount);
        clickWithdraw();
        return this;
    }

    @Step("Withdraw {amount}")
    public WithdrawPage withdraw(String amount) {
        enterAmount(amount);
        clickWithdraw();
        return this;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getResultMessage() {
        try {
            return getText(RESULT_MSG);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isTransactionSuccessful() {
        String msg = getResultMessage();
        return msg.toLowerCase().contains("transaction successful");
    }

    public boolean isInsufficientFundsError() {
        String msg = getResultMessage();
        return msg.toLowerCase().contains("transaction failed") ||
               msg.toLowerCase().contains("invalid amount");
    }

    public boolean isAmountFieldVisible() {
        return isDisplayed(AMOUNT_INPUT);
    }
}
