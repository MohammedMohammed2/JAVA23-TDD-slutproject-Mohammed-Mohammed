package org.example;

public interface BankInterface {
    //bank interface
    public User getUserById(String id);
    public boolean isCardLocked(String userId);
}
