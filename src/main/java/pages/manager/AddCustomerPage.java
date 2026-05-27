package pages.manager;

import base.BasePage;
import io.qameta.allure.Step;
import models.CustomerData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;

/**
 * "Add Customer" form within the Manager section.
 * Handles customer creation and the resulting browser alert.
 */
public class AddCustomerPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private static final By FIRST_NAME_INPUT  = By.cssSelector("input[placeholder='First Name']");
    private static final By LAST_NAME_INPUT   = By.cssSelector("input[placeholder='Last Name']");
    private static final By POST_CODE_INPUT   = By.cssSelector("input[placeholder='Post Code']");
    private static final By SUBMIT_BTN        = By.cssSelector("button[type='submit']");
    private static final By FORM_TITLE        = By.cssSelector("div.container strong");

    public AddCustomerPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Enter First Name: {firstName}")
    public AddCustomerPage enterFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
        return this;
    }

    @Step("Enter Last Name: {lastName}")
    public AddCustomerPage enterLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
        return this;
    }

    @Step("Enter Post Code: {postCode}")
    public AddCustomerPage enterPostCode(String postCode) {
        type(POST_CODE_INPUT, postCode);
        return this;
    }

    @Step("Click 'Add Customer' submit button")
    public AddCustomerPage clickAddCustomer() {
        log.info("Submitting Add Customer form");
        click(SUBMIT_BTN);
        return this;
    }

    /**
     * Fills the form and submits, then accepts the resulting alert.
     * @return the alert message text (e.g., "Customer added successfully...")
     */
    @Step("Add customer: {customer.firstName} {customer.lastName}")
    public String addCustomer(CustomerData customer) {
        enterFirstName(customer.getFirstName());
        enterLastName(customer.getLastName());
        enterPostCode(customer.getPostCode());
        clickAddCustomer();
        return acceptAlert();
    }

    /** Overload accepting raw strings — useful for invalid/edge-case data. */
    @Step("Add customer with raw data: {firstName} {lastName} / {postCode}")
    public String addCustomer(String firstName, String lastName, String postCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostCode(postCode);
        clickAddCustomer();
        return acceptAlert();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public String getFirstNameValue() {
        return getAttribute(FIRST_NAME_INPUT, "value");
    }

    public String getLastNameValue() {
        return getAttribute(LAST_NAME_INPUT, "value");
    }

    public String getPostCodeValue() {
        return getAttribute(POST_CODE_INPUT, "value");
    }

    public boolean isSubmitButtonEnabled() {
        return WaitUtils.waitForPresence(driver, SUBMIT_BTN).isEnabled();
    }

    public boolean isFormVisible() {
        return isDisplayed(FIRST_NAME_INPUT);
    }
}
