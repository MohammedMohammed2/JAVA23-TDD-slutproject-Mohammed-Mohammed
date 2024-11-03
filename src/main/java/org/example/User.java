package org.example;

public class User {
    private String id;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean isLocked;

    public User(String id, String pin, double balance) {
        this.id = id;
        this.pin = pin;
        this.balance = balance;
        this.failedAttempts = 0;
        this.isLocked = false;
    }

    // Getters och Setters
    public String getId() { return id; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public int getFailedAttempts() { return failedAttempts; }
    public boolean isLocked() { return isLocked; }

    public void lockCard() { this.isLocked = true; }
    public void incrementFailedAttempts() { this.failedAttempts++; }
    public void resetFailedAttempts() { this.failedAttempts = 0; }
    public void deposit(double amount) { this.balance += amount; }
    public void withdraw(double amount) { this.balance -= amount; }
}
