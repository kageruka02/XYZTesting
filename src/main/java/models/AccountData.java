package models;

/**
 * POJO representing a bank account (customer name + currency selection).
 */
public class AccountData {

    private String customerName;
    private String currency;
    private String accountNumber;

    public AccountData() {}

    public AccountData(String customerName, String currency) {
        this.customerName = customerName;
        this.currency = currency;
    }

    public String getCustomerName()  { return customerName; }
    public String getCurrency()      { return currency; }
    public String getAccountNumber() { return accountNumber; }

    public void setCustomerName(String customerName)   { this.customerName = customerName; }
    public void setCurrency(String currency)           { this.currency = currency; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    @Override
    public String toString() {
        return "AccountData{customer='" + customerName + "', currency='" + currency + "'}";
    }
}
