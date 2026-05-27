package pages.customer;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

import java.util.List;

/**
 * Customer account dashboard.
 * Displays account info and provides navigation to Deposit, Withdraw, and Transactions.
 * URL: #/account
 */
public class CustomerDashboard extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    // ── Locators changed to @FindBy ──────────────────────────────────────────────
    @FindBy(css = "span.ng-binding")
    private WebElement WELCOME_MSG;

    @FindBy(id = "accountSelect")
    private WebElement ACCOUNT_SELECT;

    private final By BALANCE_LABEL     = By.cssSelector("div.center strong:nth-child(2)");

    @FindBy(css = "button[ng-click='transactions()']")
    private WebElement TRANSACTIONS_BTN;

    @FindBy(css = "button[ng-click='deposit()']")
    private WebElement DEPOSIT_BTN;

    @FindBy(css = "button[ng-click='withdrawl()']")
    private WebElement WITHDRAW_BTN;

    @FindBy(css = "button[ng-click='byebye()']")
    private WebElement LOGOUT_BTN;

    @FindBy(css = "strong.error")
    private WebElement NO_ACCOUNT_MSG;


    public CustomerDashboard(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @Step("Open Deposit panel")
    public DepositPage openDeposit() {
        log.info("Clicking Deposit");
        click(DEPOSIT_BTN);
        return new DepositPage(driver);
    }

    @Step("Open Withdraw panel")
    public WithdrawPage openWithdraw() {
        log.info("Clicking Withdraw");
        click(WITHDRAW_BTN);
        return new WithdrawPage(driver);
    }

    @Step("Open Transactions view")
    public TransactionsPage openTransactions() {
        log.info("Clicking Transactions");
        click(TRANSACTIONS_BTN);
        waitForUrlContains("listTx");
        return new TransactionsPage(driver);
    }

    @Step("Logout")
    public void logout() {
        log.info("Logging out");
        click(LOGOUT_BTN);
    }

    // ── Account selection ─────────────────────────────────────────────────────

    @Step("Select account number: {accountNumber}")
    public CustomerDashboard selectAccount(String accountNumber) {
        selectByVisibleText(ACCOUNT_SELECT, accountNumber);
        return this;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getWelcomeMessage() {
        return getText(WELCOME_MSG);
    }

    public double getBalance() {
        // Balance is displayed as plain number (e.g. "1000")
        List<WebElement> labels = driver.findElements(BALANCE_LABEL);
        if (labels.isEmpty()) {
            return 0.0;
        }
        String raw = labels.get(0).getText().trim();
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            log.warn("Could not parse balance '{}', defaulting to 0", raw);
            return 0.0;
        }
    }

    public String getSelectedAccountNumber() {
        return getSelectedOption(ACCOUNT_SELECT);
    }

    public boolean isOnDashboard() {
        return getCurrentUrl().contains("account");
    }

    public boolean isDepositButtonVisible() {
        return isDisplayed(DEPOSIT_BTN);
    }

    public boolean isWithdrawButtonVisible() {
        return isDisplayed(WITHDRAW_BTN);
    }

    public boolean isTransactionsButtonVisible() {
        return isDisplayed(TRANSACTIONS_BTN);
    }

    /** True when the logged-in customer has at least one account. */
    public boolean hasAccount() {
        return !isDisplayed(NO_ACCOUNT_MSG) && isDisplayed(ACCOUNT_SELECT);
    }
}
