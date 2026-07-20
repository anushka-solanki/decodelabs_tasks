/**
 * Main class to run the ATM Management System.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the ATM application
        ATM atm = new ATM();

        // Create some sample bank accounts
        // Parameters: Account Number, PIN, Initial Balance
        BankAccount account1 = new BankAccount("123456", "1234", 1000.00);
        BankAccount account2 = new BankAccount("987654", "4321", 500.00);
        BankAccount account3 = new BankAccount("111111", "0000", 2500.00);

        // Add accounts to the ATM's internal database
        atm.addAccount(account1);
        atm.addAccount(account2);
        atm.addAccount(account3);

        System.out.println("System Initialized with 3 sample accounts.");
        System.out.println("Hint - Try using Account: '123456' with PIN: '1234'");
        
        // Start the ATM application loop
        atm.start();
    }
}
