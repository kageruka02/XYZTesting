package models;

/**
 * POJO representing a single transaction row in the transaction history table.
 */
public class TransactionData {

    private String dateTime;
    private String amount;
    private String transactionType; // "Credit" or "Debit"

    public TransactionData() {}

    public TransactionData(String dateTime, String amount, String transactionType) {
        this.dateTime = dateTime;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public String getDateTime()        { return dateTime; }
    public String getAmount()          { return amount; }
    public String getTransactionType() { return transactionType; }

    public void setDateTime(String dateTime)               { this.dateTime = dateTime; }
    public void setAmount(String amount)                   { this.amount = amount; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public double getAmountAsDouble() {
        try {
            return Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return "TransactionData{dateTime='" + dateTime + "', amount='" + amount +
               "', type='" + transactionType + "'}";
    }
}
