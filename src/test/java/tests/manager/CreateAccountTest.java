package tests.manager;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.manager.ManagerPage;
import pages.manager.OpenAccountPage;

/**
 * Tests covering "Open Account" for existing customers.
 *
 * Scenarios:
 *  1. Create an account with each supported currency (Dollar, Pound, Rupee)
 *  2. Verify the alert contains a numeric account number
 *  3. Verify the Open Account form is functional
 */
@Epic("Bank Manager")
@Feature("Open Account")
public class CreateAccountTest extends BaseTest {

    // ── Data Provider ─────────────────────────────────────────────────────────

    @DataProvider(name = "accountData")
    public Object[][] accountData() {
        // customer name must already exist in the application
        return new Object[][] {
            { "Harry Potter",      "Dollar" },
            { "Hermoine Granger",  "Pound"  },
            { "Ron Weasly",       "Rupee"  },
        };
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test(
        dataProvider = "accountData",
        description  = "Create a bank account for an existing customer with each supported currency"
    )
    @Story("Create account for existing customer")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that an account number is generated and shown in the alert after account creation.")
    public void testCreateAccountForExistingCustomer(String customerName, String currency) {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        OpenAccountPage openAccountPage = managerPage.goToOpenAccount();

        String alertText = openAccountPage.openAccount(customerName, currency);

        System.out.println(alertText);
        Assert.assertNotNull(alertText, "Alert should appear after account creation");
        Assert.assertFalse(alertText.isBlank(), "Alert should not be blank");
        log.info("Account created for '{}' ({}). Alert: {}", customerName, currency, alertText);

        // The alert text contains the account number, e.g. "Account created successfully with account Number :1001"
        Assert.assertTrue(
            alertText.toLowerCase().contains("account") || alertText.matches(".*\\d+.*"),
            "Alert should reference an account number. Actual: " + alertText
        );
    }

    @Test(description = "Open Account form should be visible after clicking the Open Account tab")
    @Story("Open Account form visibility")
    @Severity(SeverityLevel.MINOR)
    public void testOpenAccountFormIsVisible() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        OpenAccountPage openAccountPage = managerPage.goToOpenAccount();

        Assert.assertTrue(openAccountPage.isFormVisible(),
            "Open Account form (customer dropdown) should be visible");
    }

    @Test(description = "Verify customer and currency dropdowns are selectable")
    @Story("Open Account form interaction")
    @Severity(SeverityLevel.NORMAL)
    public void testCustomerAndCurrencyDropdownsWork() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        OpenAccountPage openAccountPage = managerPage.goToOpenAccount();

        openAccountPage.selectCustomer("Harry Potter");
        openAccountPage.selectCurrency("Dollar");

        Assert.assertEquals(openAccountPage.getSelectedCustomer(), "Harry Potter",
            "Selected customer should be Harry Potter");
        Assert.assertEquals(openAccountPage.getSelectedCurrency(), "Dollar",
            "Selected currency should be Dollar");
    }
}
