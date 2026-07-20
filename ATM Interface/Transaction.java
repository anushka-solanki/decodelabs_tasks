import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a single ATM transaction.
 */
public class Transaction {
    private String transactionId;
    private String type;
    private double amount;
    private Date timestamp;
    private String details;

    public Transaction(String type, double amount, String details) {
        this.transactionId = "TXN" + System.currentTimeMillis();
        this.type = type;
        this.amount = amount;
        this.timestamp = new Date();
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(timestamp);
        
        String amountStr = (amount > 0) ? String.format("$%.2f", amount) : "N/A";
        
        return String.format("[%s] %-12s | Amount: %-10s | %s (ID: %s)", 
                             formattedDate, type, amountStr, details, transactionId);
    }
}
