package pages.manager;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.WaitUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * "Customers" list page within the Manager section.
 * Allows searching for customers and deleting them.
 */
public class CustomersPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By SEARCH_INPUT      = By.cssSelector("input[ng-model='searchCustomer']");
    private static final By CUSTOMER_TABLE    = By.cssSelector("table.table");
    private static final By TABLE_ROWS        = By.cssSelector("table.table tbody tr");
    private static final By DELETE_BUTTONS    = By.cssSelector("button[ng-click='deleteCust(cust)']");
    private static final By SORT_FIRST_NAME   = By.cssSelector("a[ng-click=\"sortType = 'fName'; sortReverse = !sortReverse\"]");

    public CustomersPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Search for customer: {searchTerm}")
    public CustomersPage searchCustomer(String searchTerm) {
        log.info("Searching for customer: {}", searchTerm);
        WebElement input = WaitUtils.waitForVisible(driver, SEARCH_INPUT);
        input.clear();
        input.sendKeys(searchTerm);
        return this;
    }

    @Step("Delete customer: {customerName}")
    public CustomersPage deleteCustomer(String customerName) {
        searchCustomer(customerName);
        List<WebElement> rows = WaitUtils.waitForAllVisible(driver, TABLE_ROWS);
        for (WebElement row : rows) {
            if (row.getText().contains(customerName)) {
                WebElement deleteBtn = row.findElement(By.cssSelector("button[ng-click='deleteCust(cust)']"));
                scrollIntoView(deleteBtn);
                click(deleteBtn);
                log.info("Deleted customer: {}", customerName);
                return this;
            }
        }
        throw new RuntimeException("Customer not found in the list: " + customerName);
    }

    @Step("Clear search field")
    public CustomersPage clearSearch() {
        WaitUtils.waitForVisible(driver, SEARCH_INPUT).clear();
        return this;
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Returns the full names of all customers currently visible in the table.
     * Full name = first-name column + last-name column text.
     */
    public List<String> getVisibleCustomerNames() {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        return rows.stream()
                   .map(row -> {
                       List<WebElement> cells = row.findElements(By.tagName("td"));
                       if (cells.size() >= 2) {
                           return (cells.get(0).getText() + " " + cells.get(1).getText()).trim();
                       }
                       return "";
                   })
                   .filter(name -> !name.isBlank())
                   .collect(Collectors.toList());
    }

    public boolean isCustomerVisible(String customerName) {
        return getVisibleCustomerNames().stream()
                                        .anyMatch(name -> name.startsWith(customerName));
    }

    public int getCustomerCount() {
        return driver.findElements(TABLE_ROWS).size();
    }

    public boolean isTableVisible() {
        return isDisplayed(CUSTOMER_TABLE);
    }

    public boolean isNoCustomerRowPresent() {
        List<WebElement> rows = driver.findElements(TABLE_ROWS);
        return rows.isEmpty();
    }
}
