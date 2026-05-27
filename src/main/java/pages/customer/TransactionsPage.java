package pages.customer;

import base.BasePage;
import io.qameta.allure.Step;
import models.TransactionData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction history page for a customer account.
 * URL: #/listTx
 *
 * The table is read-only — there are no editable fields, confirming
 * that transaction history cannot be modified by the customer.
 */
public class TransactionsPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By TABLE_ROWS       = By.cssSelector("table.table tbody tr");
    private static final By TABLE_HEADERS    = By.cssSelector("table.table thead tr th");
    private static final By RESET_BTN        = By.cssSelector("button[ng-click='reset()']");
    private static final By BACK_BTN         = By.cssSelector("button[ng-click='back()']");


    public TransactionsPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Click Reset to clear transaction filter")
    public TransactionsPage clickReset() {
        log.info("Clicking Reset");
        click(RESET_BTN);
        return this;
    }

    @Step("Click Back to return to dashboard")
    public CustomerDashboard clickBack() {
        log.info("Clicking Back");
        click(BACK_BTN);
        waitForUrlContains("account");
        return new CustomerDashboard(driver);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Parses and returns all transaction rows from the table.
     * Columns: Date-Time | Amount | Transaction Type
     */
    @Step("Read all transactions from table")
    public List<TransactionData> getAllTransactions() {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        List<TransactionData> transactions = new ArrayList<>();

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                transactions.add(new TransactionData(
                    cells.get(0).getText().trim(),
                    cells.get(1).getText().trim(),
                    cells.get(2).getText().trim()
                ));
            }
        }
        log.info("Found {} transactions", transactions.size());
        return transactions;
    }

    public int getTransactionCount() {
        return driver.findElements(TABLE_ROWS).size();
    }

    /**
     * Verifies the table has no input fields, confirming read-only state.
     */
    public boolean isTableReadOnly() {
        List<WebElement> inputs = driver.findElements(
            By.cssSelector("table.table tbody input")
        );
        return inputs.isEmpty();
    }

    public boolean hasTransactions() {
        return getTransactionCount() > 0;
    }

    public boolean isOnTransactionsPage() {
        return getCurrentUrl().contains("listTx");
    }

    /**
     * Checks that a transaction of the given type and amount is present in the list.
     */
    public boolean containsTransaction(String type, String amount) {
        return getAllTransactions().stream()
            .anyMatch(tx -> tx.getTransactionType().equalsIgnoreCase(type)
                         && tx.getAmount().equals(amount));
    }

    /**
     * Returns the column headers to confirm expected table structure.
     */
    public List<String> getTableHeaders() {
        List<WebElement> headers = driver.findElements(TABLE_HEADERS);
        List<String> headerText = new ArrayList<>();
        for (WebElement h : headers) {
            headerText.add(h.getText().trim());
        }
        return headerText;
    }
}
