package org.example;

import java.util.HashMap;
import java.util.Map;

public class Bank implements BankInterface {
    private Map<String, User> users = new HashMap<>();

    public User getUserById(String id) {
        return users.get(id);
    }

    public boolean isCardLocked(String userId) {
        User user = users.get(userId);
        return user != null && user.isLocked();
    }

    public static String getBankName() {
        return "Nordia";
    }
}