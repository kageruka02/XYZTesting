package pages.customer;

import base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import utils.WaitUtils;

public class CustomerLoginPage extends BasePage {

    // ── Locators changed to @FindBy ──────────────────────────────────────────────
    @FindBy(id = "userSelect")
    private WebElement customerSelect;

    // ── Locators ──────────────────────────────────────────────────────────────
    @FindBy(css = "button.btn.btn-default[type='submit']")
    private WebElement loginBtn;



    @FindBy(css = "button[ng-click='cancel()']")
    private WebElement backBtn;

    public CustomerLoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Select customer: {customerName}")
    public CustomerLoginPage selectCustomer(String customerName) {
        log.info("Selecting customer from dropdown: {}", customerName);
        waitForDropdownPopulated(customerSelect);
       Select select =   new Select(customerSelect);
        for (WebElement option : select.getOptions()) {
            System.out.println("Option: '" + option.getText() + "'");
        }
        select.selectByVisibleText(customerName);
        return this;
    }

    @Step("Click Login button")
    public CustomerDashboard clickLogin() {
        log.info("Clicking Login");
        // Wait until the login button is clickable
        WaitUtils.waitForClickable(driver, loginBtn).click();

        waitForUrlContains("account");
        return new CustomerDashboard(driver);
    }

    @Step("Login as customer: {customerName}")
    public CustomerDashboard loginAs(String customerName) {
        selectCustomer(customerName);
        return clickLogin();
    }

    @Step("Click Back to home")
    public void clickBack() {
        backBtn.click();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isLoginButtonEnabled() {
        try {
            return loginBtn.isDisplayed() && loginBtn.getAttribute("disabled") == null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCustomerDropdownVisible() {
        return customerSelect.isDisplayed();
    }

    public String getSelectedCustomer() {
        return new Select(customerSelect).getFirstSelectedOption().getText();
    }

    // ── Angular-aware helpers ─────────────────────────────────────────────────

    private void waitForDropdownPopulated(WebElement selectElement) {
        WaitUtils.waitFor(driver, d -> {
            try {
                Select sel = new Select(selectElement);
                return sel.getOptions().size() > 1;
            } catch (Exception e) {
                return false;
            }
        }, 15);
    }

    private void waitForButtonEnabled(WebElement buttonElement) {
        WaitUtils.waitFor(driver, d -> {
            try {
                return buttonElement.isDisplayed() && buttonElement.isEnabled();
            } catch (Exception e) {
                return false;
            }
        }, 15);
    }
}
