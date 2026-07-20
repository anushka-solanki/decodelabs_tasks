import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Manages the user interface and interaction flow of the ATM.
 */
public class ATM {
    private Map<String, BankAccount> accountsDatabase;
    private Scanner scanner;
    private BankAccount currentAccount;

    public ATM() {
        this.accountsDatabase = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.currentAccount = null;
    }

    /**
     * Adds an account to the simulated bank database.
     */
    public void addAccount(BankAccount account) {
        accountsDatabase.put(account.getAccountNumber(), account);
    }

    /**
     * Starts the ATM application loop.
     */
    public void start() {
        System.out.println("============================================");
        System.out.println("   Welcome to the ATM Management System     ");
        System.out.println("============================================");

        while (true) {
            if (currentAccount == null) {
                authenticateUser();
            } else {
                showMainMenu();
            }
        }
    }

    /**
     * Handles the secure PIN-based login process.
     */
    private void authenticateUser() {
        System.out.println("\n--- Please Log In ---");
        System.out.print("Enter Account Number (or 'exit' to quit): ");
        String accNum = scanner.nextLine().trim();
        
        if (accNum.equalsIgnoreCase("exit")) {
            System.out.println("Thank you for using the ATM. Goodbye!");
            System.exit(0);
        }

        BankAccount account = accountsDatabase.get(accNum);
        if (account == null) {
            System.out.println("Error: Account not found.");
            return;
        }

        if (account.isLocked()) {
            System.out.println("Error: Account is LOCKED due to multiple failed login attempts.");
            System.out.println("Please contact your branch to unlock the account.");
            return;
        }

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine().trim();

        if (account.authenticate(pin)) {
            System.out.println("\nLogin Successful! Welcome.");
            currentAccount = account;
        } else {
            System.out.println("Error: Invalid PIN.");
            if (account.isLocked()) {
                System.out.println("WARNING: You have exceeded the maximum number of failed attempts. Your account is now LOCKED.");
            } else {
                System.out.println("Please try again.");
            }
        }
    }

    /**
     * Displays the main dashboard and handles user selection.
     */
    private void showMainMenu() {
        System.out.println("\n============================================");
        System.out.println("                 ATM MENU                   ");
        System.out.println("============================================");
        System.out.println("1. Check Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. Transaction History");
        System.out.println("6. Logout");
        System.out.println("============================================");
        System.out.print("Select an option (1-6): ");

        String choice = scanner.nextLine().trim();
        
        try {
            int option = Integer.parseInt(choice);
            switch (option) {
                case 1:
                    checkBalance();
                    break;
                case 2:
                    performDeposit();
                    break;
                case 3:
                    performWithdrawal();
                    break;
                case 4:
                    performTransfer();
                    break;
                case 5:
                    showTransactionHistory();
                    break;
                case 6:
                    logout();
                    break;
                default:
                    System.out.println("Invalid option. Please choose a number between 1 and 6.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void checkBalance() {
        System.out.printf("\nYour current balance is: $%.2f\n", currentAccount.getBalance());
    }

    private void performDeposit() {
        System.out.print("\nEnter amount to deposit: $");
        double amount = getValidAmount();
        
        if (amount > 0) {
            try {
                currentAccount.deposit(amount);
                System.out.printf("Successfully deposited $%.2f.\n", amount);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void performWithdrawal() {
        System.out.print("\nEnter amount to withdraw: $");
        double amount = getValidAmount();
        
        if (amount > 0) {
            try {
                boolean success = currentAccount.withdraw(amount);
                if (success) {
                    System.out.printf("Successfully withdrew $%.2f.\n", amount);
                    System.out.println("Please take your cash.");
                } else {
                    System.out.println("Error: Insufficient funds. Transaction cancelled.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void performTransfer() {
        System.out.print("\nEnter Target Account Number: ");
        String targetAccNum = scanner.nextLine().trim();
        
        if (targetAccNum.equals(currentAccount.getAccountNumber())) {
            System.out.println("Error: You cannot transfer money to your own account.");
            return;
        }
        
        BankAccount targetAccount = accountsDatabase.get(targetAccNum);
        if (targetAccount == null) {
            System.out.println("Error: Target account not found.");
            return;
        }
        
        System.out.print("Enter amount to transfer: $");
        double amount = getValidAmount();
        
        if (amount > 0) {
            try {
                boolean success = currentAccount.transfer(targetAccount, amount);
                if (success) {
                    System.out.printf("Successfully transferred $%.2f to account %s.\n", amount, targetAccNum);
                } else {
                    System.out.println("Error: Insufficient funds. Transfer cancelled.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        System.out.println(currentAccount.getTransactionHistoryAsString());
        System.out.println("---------------------------");
    }

    private void logout() {
        System.out.println("\nLogging out... Thank you for using our ATM.");
        currentAccount = null;
    }
    
    /**
     * Helper method to securely get a valid double amount from the user.
     */
    private double getValidAmount() {
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                System.out.println("Error: Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid numeric format. Please enter a valid amount.");
            return -1;
        }
    }
}
