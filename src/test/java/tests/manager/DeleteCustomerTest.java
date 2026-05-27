package tests.manager;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.manager.AddCustomerPage;
import pages.manager.CustomersPage;
import pages.manager.ManagerPage;

/**
 * Tests covering customer deletion by the Bank Manager.
 *
 * Scenarios:
 *  1. Delete an existing customer and verify they no longer appear in the list
 *  2. Search for a deleted customer — no results
 *  3. Customers list is visible on the Customers tab
 */
@Epic("Bank Manager")
@Feature("Delete Customer")
public class DeleteCustomerTest extends BaseTest {

    private static final String TEMP_FIRST = "DeleteMe";
    private static final String TEMP_LAST  = "TestUser";
    private static final String TEMP_POST  = "D3L3T3";

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test(description = "Add a customer, then delete them and verify removal from the list")
    @Story("Delete customer")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Creates a throwaway customer, deletes them, and asserts they are absent from the customer list.")
    public void testDeleteCustomerRemovedFromList() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();

        // Step 1: add a temporary customer
        AddCustomerPage addPage = managerPage.goToAddCustomer();
        addPage.addCustomer(TEMP_FIRST, TEMP_LAST, TEMP_POST);

        // Step 2: navigate to Customers list and delete
        CustomersPage customersPage = managerPage.goToCustomers();
        String fullName = TEMP_FIRST + " ";

        Assert.assertTrue(customersPage.isCustomerVisible(fullName),
            "Newly added customer '" + fullName + "' should appear in the list before deletion");

        customersPage.deleteCustomer(fullName);

        // Step 3: verify the customer is gone
        customersPage.clearSearch();
        Assert.assertFalse(customersPage.isCustomerVisible(fullName),
            "Deleted customer '" + fullName + "' should not appear in the list");
    }

    @Test(description = "Search for a non-existent customer returns no results")
    @Story("Search customer")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchForDeletedCustomerReturnsEmpty() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        CustomersPage customersPage = managerPage.goToCustomers();

        // Search for a name that definitely does not exist
        customersPage.searchCustomer("DefinitelyNotExist_XYZ_9999");

        Assert.assertFalse(customersPage.isCustomerVisible("DefinitelyNotExist_XYZ_9999"),
            "Search for a non-existent customer should return zero rows");
    }

    @Test(description = "Customers list table is visible on the Customers tab")
    @Story("Customers list visibility")
    @Severity(SeverityLevel.MINOR)
    public void testCustomersListTableIsVisible() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        CustomersPage customersPage = managerPage.goToCustomers();

        Assert.assertTrue(customersPage.isTableVisible(),
            "Customer table should be visible when navigating to the Customers tab");
    }

    @Test(description = "Customer list shows at least one pre-seeded customer")
    @Story("Pre-seeded customer data")
    @Severity(SeverityLevel.MINOR)
    public void testPreSeededCustomersExist() {
        ManagerPage managerPage = openHomePage().clickBankManagerLogin();
        CustomersPage customersPage = managerPage.goToCustomers();

        int count = customersPage.getCustomerCount();
        Assert.assertTrue(count > 0,
            "The XYZ Bank app should have at least one pre-seeded customer. Found: " + count);
    }
}
