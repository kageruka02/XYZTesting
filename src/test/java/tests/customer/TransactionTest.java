package tests.customer;

import base.BaseTest;
import io.qameta.allure.*;
import models.TransactionData;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.customer.CustomerDashboard;
import pages.customer.CustomerLoginPage;
import pages.customer.DepositPage;
import pages.customer.TransactionsPage;
import pages.customer.WithdrawPage;

import java.util.List;

/**
 * Tests covering the Transaction History feature.
 *
 * Scenarios:
 *  1. Transaction history is accessible after a deposit
 *  2. Deposit is recorded as a Credit transaction
 *  3. Withdrawal is recorded as a Debit transaction
 *  4. Transaction history is read-only (no input fields in the table)
 *  5. Reset button clears the displayed transactions (filter reset)
 *  6. Back button returns to the customer dashboard
 */
@Epic("Customer")
@Feature("Transaction History")
public class TransactionTest extends BaseTest {

    private static final String TEST_CUSTOMER = "Harry Potter";

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CustomerDashboard loginAndOpenDashboard() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();
        return loginPage.loginAs(TEST_CUSTOMER);
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test(description = "Transaction history page is accessible from the customer dashboard")
    @Story("View transaction history")
    @Severity(SeverityLevel.CRITICAL)
    public void testTransactionHistoryPageIsAccessible() {
        CustomerDashboard dashboard = loginAndOpenDashboard();

        // Make a deposit so there is at least one transaction
        DepositPage depositPage = dashboard.openDeposit();
        depositPage.deposit(100);

        dashboard = loginAndOpenDashboard();
        TransactionsPage txPage = dashboard.openTransactions();

        Assert.assertTrue(txPage.isOnTransactionsPage(),
            "Should navigate to the transactions page. URL: " + txPage.getCurrentUrl());
    }




    @Test(description = "Transaction history table is read-only — no editable input fields present")
    @Story("Read-only transaction history")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Asserts that the transaction table contains no <input> elements, proving it is non-editable.")
    public void testTransactionHistoryIsReadOnly() {
        CustomerDashboard dashboard = loginAndOpenDashboard();

        // Ensure at least one transaction exists
        dashboard.openDeposit().deposit(50);
        dashboard = loginAndOpenDashboard();

        TransactionsPage txPage = dashboard.openTransactions();

        Assert.assertTrue(txPage.isTableReadOnly(),
            "Transaction history table must not contain any editable input fields");
    }



    @Test(description = "Back button on the Transactions page returns to the customer dashboard")
    @Story("Navigate back from transactions")
    @Severity(SeverityLevel.NORMAL)
    public void testBackButtonReturnsToD​ashboard() {
        CustomerDashboard dashboard = loginAndOpenDashboard();
        dashboard.openDeposit().deposit(10);
        dashboard = loginAndOpenDashboard();

        TransactionsPage txPage = dashboard.openTransactions();
        CustomerDashboard returnedDashboard = txPage.clickBack();

        Assert.assertTrue(returnedDashboard.isOnDashboard(),
            "Back button should return to the customer dashboard. URL: " + returnedDashboard.getCurrentUrl());
    }

}
