import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's bank account with basic operations.
 */
public class BankAccount {
    private String accountNumber;
    private String pin;
    private double balance;
    
    private boolean isLocked;
    private int failedAttempts;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    
    private List<Transaction> transactionHistory;

    public BankAccount(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialBalance;
        this.isLocked = false;
        this.failedAttempts = 0;
        this.transactionHistory = new ArrayList<>();
        
        // Initial deposit transaction
        if (initialBalance > 0) {
            addTransaction("Initial", initialBalance, "Account created with initial balance");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Authenticates the user. Handles account locking upon too many failed attempts.
     */
    public boolean authenticate(String inputPin) {
        if (isLocked) {
            return false;
        }
        
        if (this.pin.equals(inputPin)) {
            failedAttempts = 0; // Reset counter on success
            return true;
        } else {
            failedAttempts++;
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                isLocked = true;
            }
            return false;
        }
    }

    /**
     * Deposits a specified amount into the account.
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        addTransaction("Deposit", amount, "Successful deposit");
    }

    /**
     * Withdraws a specified amount if sufficient funds exist.
     */
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            addTransaction("Failed W/D", amount, "Insufficient funds for withdrawal");
            return false;
        }
        balance -= amount;
        addTransaction("Withdrawal", amount, "Successful withdrawal");
        return true;
    }
    
    /**
     * Transfers a specified amount to a target account.
     */
    public boolean transfer(BankAccount targetAccount, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (amount > balance) {
            addTransaction("Failed Tfr", amount, "Insufficient funds to transfer to " + targetAccount.getAccountNumber());
            return false;
        }
        
        // Perform transfer
        this.balance -= amount;
        targetAccount.balance += amount;
        
        // Record transactions for both accounts
        this.addTransaction("Transfer Out", amount, "Transferred to " + targetAccount.getAccountNumber());
        targetAccount.addTransaction("Transfer In", amount, "Received from " + this.accountNumber);
        
        return true;
    }

    private void addTransaction(String type, double amount, String details) {
        Transaction t = new Transaction(type, amount, details);
        transactionHistory.add(t);
    }

    /**
     * Returns a formatted string of the transaction history.
     */
    public String getTransactionHistoryAsString() {
        if (transactionHistory.isEmpty()) {
            return "No transactions found.";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactionHistory) {
            sb.append(t.toString()).append("\n");
        }
        return sb.toString();
    }
}
