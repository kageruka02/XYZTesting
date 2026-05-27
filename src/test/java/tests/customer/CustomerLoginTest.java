package tests.customer;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.customer.CustomerDashboard;
import pages.customer.CustomerLoginPage;

/**
 * Tests covering customer authentication.
 *
 * Scenarios:
 *  1. Login with valid credentials (data-driven for multiple customers)
 *  2. Verify dashboard is displayed after login
 *  3. Verify logout returns to the home page
 */
@Epic("Customer")
@Feature("Customer Login")
public class CustomerLoginTest extends BaseTest {

    @DataProvider(name = "validCustomers")
    public Object[][] validCustomers() {
        return new Object[][] {
            { "Harry Potter"      },
            { "Hermoine Granger"  },
            { "Ron Weasly"       },
            { "Albus Dumbledore"  },
            { "Neville Longbottom"},
        };
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test(
        dataProvider = "validCustomers",
        description  = "Login with a valid customer name and verify the dashboard loads"
    )
    @Story("Valid customer login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Selects a customer from the dropdown and clicks Login; verifies the account dashboard appears.")
    public void testLoginWithValidCustomer(String customerName) {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();
        CustomerDashboard dashboard = loginPage.loginAs(customerName);

        Assert.assertTrue(dashboard.isOnDashboard(),
            "Should land on the customer dashboard after login. URL: " + dashboard.getCurrentUrl());

        String welcome = dashboard.getWelcomeMessage();
        Assert.assertFalse(welcome.isBlank(),
            "Welcome message should not be blank after login for: " + customerName);

        log.info("Customer '{}' logged in. Welcome: {}", customerName, welcome);
    }

    @Test(description = "Login button should be disabled before a customer is selected")
    @Story("Customer login form state")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginButtonDisabledBeforeSelection() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();

        Assert.assertFalse(loginPage.isLoginButtonEnabled(),
            "Login button must be disabled when no customer is selected");
    }

    @Test(description = "Customer dropdown is visible on the login page")
    @Story("Customer login page UI")
    @Severity(SeverityLevel.MINOR)
    public void testCustomerDropdownVisible() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();

        Assert.assertTrue(loginPage.isCustomerDropdownVisible(),
            "Customer dropdown should be visible on the login page");
    }

    @Test(description = "Logout from dashboard returns to home page")
    @Story("Customer logout")
    @Severity(SeverityLevel.CRITICAL)
    public void testLogoutReturnsToHomePage() {
        CustomerLoginPage loginPage = openHomePage().clickCustomerLogin();
        CustomerDashboard dashboard = loginPage.loginAs("Harry Potter");

        dashboard.logout();

        String url = getDriver().getCurrentUrl();
        Assert.assertTrue(
            url.contains("login") || url.contains("BankingProject"),
            "After logout, should redirect to the home/login page. URL: " + url
        );
    }
}
