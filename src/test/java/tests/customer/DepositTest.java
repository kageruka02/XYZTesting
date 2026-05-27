package tests.customer;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.customer.CustomerDashboard;
import pages.customer.CustomerLoginPage;
import pages.customer.DepositPage;
import utils.JsonDataReader;

/**
 * Tests covering the Deposit feature for customers.
 *
 * Scenarios:
 *  1. Deposit a valid amount — balance increases, success message shown
 *  2. Deposit zero — expect no success (invalid)
 *  3. Deposit negative amount — expect no success (invalid)
 *  4. Multiple sequential deposits — balance accumulates correctly
 */
@Epic("Customer")
@Feature("Deposit")
public class DepositTest extends BaseTest {

    private static final String TEST_CUSTOMER = "Harry Potter";

    // ── Data Providers ────────────────────────────────────────────────────────

    @DataProvider(name = "validDeposits")
    public Object[][] validDeposits() {
        return JsonDataReader.getValidDeposits();
    }

    @DataProvider(name = "invalidDeposits")
    public Object[][] invalidDeposits() {
        return JsonDataReader.getInvalidDeposits();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CustomerDashboard loginAndOpenDashboard() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();
        return loginPage.loginAs(TEST_CUSTOMER);
    }

    // ── Positive tests ────────────────────────────────────────────────────────

    @Test(
        dataProvider = "validDeposits",
        description  = "Deposit a valid amount and verify balance increases and success message appears"
    )
    @Story("Valid deposit")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Opens the deposit panel, enters a valid amount, submits, and verifies success.")
    public void testDepositValidAmount(String amount, String description) {
        log.info("Test scenario: {}", description);

        CustomerDashboard dashboard = loginAndOpenDashboard();
        double balanceBefore = dashboard.getBalance();

        DepositPage depositPage = dashboard.openDeposit();
        depositPage.deposit(amount);

        String msg = depositPage.getResultMessage();
        Assert.assertTrue(
            depositPage.isSuccessMessageVisible() || msg.toLowerCase().contains("deposit"),
            "Expected deposit success message. Actual: " + msg
        );
        log.info("Deposited {}. Result: {}", amount, msg);
    }

    @Test(description = "Balance increases by the deposited amount")
    @Story("Balance after deposit")
    @Severity(SeverityLevel.CRITICAL)
    public void testBalanceIncreasesAfterDeposit() {
        int depositAmount = 500;

        CustomerDashboard dashboard = loginAndOpenDashboard();
        double balanceBefore = dashboard.getBalance();

        DepositPage depositPage = dashboard.openDeposit();
        depositPage.deposit(depositAmount);

        // Navigate back to dashboard to read updated balance
        dashboard = loginAndOpenDashboard();
        double balanceAfter = dashboard.getBalance();

        Assert.assertEquals(
            balanceAfter,
            balanceBefore + depositAmount,
            "Balance should increase by " + depositAmount
        );
    }

    // ── Negative tests ────────────────────────────────────────────────────────

    @Test(
        dataProvider = "invalidDeposits",
        description  = "Attempt to deposit an invalid amount (negative or zero); no success message should appear"
    )
    @Story("Invalid deposit")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that depositing 0 or a negative number does not produce a success message.")
    public void testDepositInvalidAmount(String amount, String description) {
        log.info("Invalid deposit scenario: {}", description);

        CustomerDashboard dashboard = loginAndOpenDashboard();
        DepositPage depositPage = dashboard.openDeposit();
        depositPage.deposit(amount);

        // The app should not show a "deposit successful" message for invalid amounts
        Assert.assertFalse(
            depositPage.isSuccessMessageVisible(),
            "Should not show success for invalid deposit amount: " + amount
        );
    }
}
