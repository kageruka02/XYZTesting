package tests.customer;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.customer.CustomerDashboard;
import pages.customer.CustomerLoginPage;
import pages.customer.DepositPage;
import pages.customer.WithdrawPage;

/**
 * Tests covering the Withdraw feature for customers.
 *
 * Scenarios:
 *  1. Withdraw a valid amount (less than balance) — success message shown
 *  2. Withdraw entire balance — success message shown
 *  3. Withdraw more than balance — failure/error message shown
 *  4. Verify balance decreases by withdrawn amount
 */
@Epic("Customer")
@Feature("Withdraw")
public class WithdrawTest extends BaseTest {

    private static final String TEST_CUSTOMER = "Harry Potter";

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CustomerDashboard loginAndOpenDashboard() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();
        return loginPage.loginAs(TEST_CUSTOMER);
    }

    /**
     * Seeds a known balance by making a deposit, then returns the dashboard.
     */
    private CustomerDashboard seedBalance(int amount) {
        CustomerDashboard dashboard = loginAndOpenDashboard();
        DepositPage depositPage = dashboard.openDeposit();
        depositPage.deposit(amount);
        return loginAndOpenDashboard(); // fresh dashboard with updated balance
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test(description = "Withdraw a partial amount from a funded account")
    @Story("Valid withdrawal")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Deposits 1000, withdraws 500, expects success message.")
    public void testWithdrawValidAmount() {
        int deposit = 1000;
        int withdraw = 500;

        CustomerDashboard dashboard = seedBalance(deposit);
        WithdrawPage withdrawPage = dashboard.openWithdraw();
        withdrawPage.withdraw(withdraw);

        Assert.assertTrue(
            withdrawPage.isTransactionSuccessful(),
            "Should show transaction successful after valid withdrawal. Actual: " + withdrawPage.getResultMessage()
        );
    }

    @Test(description = "Withdraw the full account balance")
    @Story("Full balance withdrawal")
    @Severity(SeverityLevel.NORMAL)
    public void testWithdrawFullBalance() {
        int amount = 800;

        CustomerDashboard dashboard = seedBalance(amount);
        double balance = dashboard.getBalance();
        int withdrawAmount = (int) balance;

        WithdrawPage withdrawPage = dashboard.openWithdraw();
        withdrawPage.withdraw(withdrawAmount);

        Assert.assertTrue(
            withdrawPage.isTransactionSuccessful(),
            "Should allow withdrawing the full balance. Actual: " + withdrawPage.getResultMessage()
        );
    }

    @Test(description = "Withdraw more than the available balance — transaction should fail")
    @Story("Insufficient balance withdrawal")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Deposits 100, then attempts to withdraw 999. Expects a failure/error response.")
    public void testWithdrawWithInsufficientBalance() {
        int deposit  = 100;
        int withdraw = 999;

        CustomerDashboard dashboard = seedBalance(deposit);
        WithdrawPage withdrawPage = dashboard.openWithdraw();
        withdrawPage.withdraw(withdraw);

        // Should NOT show a success message; should show an error or no message
        Assert.assertFalse(
            withdrawPage.isTransactionSuccessful(),
            "Withdrawal should fail when amount exceeds balance"
        );
        log.info("Insufficient balance result: {}", withdrawPage.getResultMessage());
    }

    @Test(description = "Balance decreases by the withdrawn amount after a successful withdrawal")
    @Story("Balance after withdrawal")
    @Severity(SeverityLevel.CRITICAL)
    public void testBalanceDecreasesAfterWithdrawal() {
        int deposit  = 2000;
        int withdraw = 600;

        CustomerDashboard dashboard = seedBalance(deposit);
        double balanceBefore = dashboard.getBalance();

        WithdrawPage withdrawPage = dashboard.openWithdraw();
        withdrawPage.withdraw(withdraw);

        // Re-login to read fresh balance
        dashboard = loginAndOpenDashboard();
        double balanceAfter = dashboard.getBalance();

        Assert.assertEquals(
            balanceAfter,
            balanceBefore - withdraw,
            "Balance should decrease by " + withdraw + " after withdrawal"
        );
    }

    @Test(description = "Withdraw zero amount — should not be treated as a successful transaction")
    @Story("Invalid withdrawal amount")
    @Severity(SeverityLevel.NORMAL)
    public void testWithdrawZeroAmount() {
        CustomerDashboard dashboard = loginAndOpenDashboard();
        WithdrawPage withdrawPage = dashboard.openWithdraw();
        withdrawPage.withdraw(0);

        Assert.assertFalse(
            withdrawPage.isTransactionSuccessful(),
            "Withdrawing zero should not produce a success message"
        );
    }
}
