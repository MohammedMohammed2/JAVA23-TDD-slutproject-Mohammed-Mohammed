package org.example;

import java.util.Scanner;

public class ATM {
    private Bank bank;
    private User currentUser;
    private BankInterface bankInterface;

    private int numberOftries = 3;

    //constructor to initialize
    public ATM(Bank bank, User currentUser, BankInterface bankInterface) {
        this.bank = bank;
        this.bankInterface = bankInterface;
        this.currentUser = currentUser;
    }
    //if the user is not null then it will assigns the User object to the currentUser and retun true and if the user is null it will retrun false
    public boolean insertCard(String userId) {
        User user = bankInterface.getUserById(userId);
        if (user != null) {
            currentUser = user;
            return true;
        } else {
            currentUser = null;
            return false;
        }
    }

// it checks if the  card of the user is not locked and it calculate the amount of fail attempts the usre is allowed to make before locking the card
    public boolean enterPin(String pin) {
        if (currentUser != null) {
            if (currentUser.isLocked()) {
                System.out.println("Card is locked. Please contact the bank.");
                return false;
            }

            if (currentUser.getPin().equals(pin)) {
                currentUser.resetFailedAttempts();
                System.out.println("PIN accepted. Access granted.");
                return true;
            } else {
                currentUser.incrementFailedAttempts();
                numberOftries--;
                System.out.println("Card locked after "+ numberOftries +" failed attempts.");
                if (currentUser.getFailedAttempts() == 3) {
                    currentUser.lockCard();
                    return false;
                }
                return false;
            }
        }
        return false;
    }
    //checks if the user is not null and shows the user their balance
    public double checkBalance() {
        if (currentUser != null) {
            return currentUser.getBalance();
        } else {
            System.out.println("No user authenticated.");
            return -1;
        }
    }
    //checks if the user is not null and deposits the money
    public void deposit(double amount) {
        if (currentUser != null) {
            currentUser.deposit(amount);
            System.out.println("Deposit successful. New balance: " + currentUser.getBalance());
        } else {
            System.out.println("No user authenticated.");
        }
    }


    public boolean withdraw(double amount) {
        if (currentUser != null && currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        User testUser = new User("user123", "5678", 1000.0);


        //checks if test user match the given id and if the card is locked
        BankInterface bankInterface = new BankInterface() {
            @Override
            public User getUserById(String id) {
                return id.equals(testUser.getId()) ? testUser : null;
            }

            @Override
            public boolean isCardLocked(String userId) {
                return testUser.isLocked();
            }
        };


        ATM atm = new ATM(new Bank(), testUser, bankInterface);

        Scanner inputScanner = new Scanner(System.in);

        while (true) {
            System.out.print("insert your card (Enter user ID or type exit to quit the application): ");
            String userId = inputScanner.nextLine();
            if (userId.equalsIgnoreCase("exit")) {
                break;
            }
            //passes the id through insert card and check if the pin is entered
            if (atm.insertCard(userId)) {
                boolean pinValidated = false;
                while (!pinValidated) {
                    System.out.print("Enter your PIN: ");
                    String pin = inputScanner.nextLine();
                    pinValidated = atm.enterPin(pin);
                }
                //login menu
                while (true) {
                    System.out.println("Select an option: 1-Withdraw || 2-Deposit || 3-Check Balance || 4- Exit");
                    int choice = inputScanner.nextInt();
                    inputScanner.nextLine();
                    switch (choice) {
                        case 1:
                            System.out.print("Enter amount to withdraw: ");
                            double amountToWithdraw = inputScanner.nextDouble();
                            if (atm.withdraw(amountToWithdraw)) {
                                System.out.println("Withdrawal successful. New balance: $" + atm.checkBalance());
                            } else {
                                System.out.println("Withdrawal failed. Insufficient funds.");
                            }
                            break;
                        case 2:
                            System.out.print("Enter amount to deposit: ");
                            double amountToDeposit = inputScanner.nextDouble();
                            atm.deposit(amountToDeposit);
                            break;
                        case 3:
                            System.out.println("Your balance is: $" + atm.checkBalance());
                            break;
                        case 4:
                            System.out.println("Exiting.");
                            return;
                        default:
                            System.out.println("Invalid option, please try again.");
                    }
                }
            } else {
                System.out.println("Invalid card or access denied.");
            }
        }
    }
}
