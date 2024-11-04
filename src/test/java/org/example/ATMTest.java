package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class ATMTest {
    private Bank mockBank;
    private User mockUser;
    private BankInterface mockBankInterface;
    private ATM atm;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        mockBank = mock(Bank.class);
        mockUser = mock(User.class);
        mockBankInterface = mock(BankInterface.class);
        atm = new ATM(mockBank, mockUser, mockBankInterface);
    }

    @Test
    @DisplayName("test when teh user exist")
    public void testUserFound() {
        String userId = "user456";
        when(mockBankInterface.getUserById(userId)).thenReturn(mockUser);
        boolean result = atm.insertCard(userId);
        assertTrue(result, "The card should be accepted for a valid user ID.");
        verify(mockBankInterface).getUserById(userId);
    }

    @Test
    @DisplayName("test when the user dosent exist")
    public void testUserNotFound() {
        String userId = "user456";
        when(mockBankInterface.getUserById(userId)).thenReturn(null);
        boolean result = atm.insertCard(userId);
        assertFalse(result, "The card should not be accepted for an invalid user ID.");
        verify(mockBankInterface).getUserById(userId);
    }

    @Test
    @DisplayName("test when the pin code is entered correctly")
    public void testEnterCorrectPin() {
        String correctPin = "5678";
        when(mockUser.getPin()).thenReturn(correctPin);
        boolean result = atm.enterPin(correctPin);
        assertTrue(result, "The PIN should be accepted if it matches the user's PIN.");
        verify(mockUser).getPin();
        verify(mockUser).resetFailedAttempts();
    }

    @Test
    @DisplayName("test whenthe pin entered is incorrect")
    public void testEnterIncorrectPin() {
        String correctPin = "5678";
        String incorrectPin = "8765";
        when(mockUser.getPin()).thenReturn(correctPin);
        boolean result = atm.enterPin(incorrectPin);
        assertFalse(result, "The PIN should not be accepted if it does not match the user's PIN.");
        verify(mockUser).getPin();
        verify(mockUser).incrementFailedAttempts();
    }

    @Test
    @DisplayName("test check balance")
    public void testCheckBalance() {
        double balance = 1000.0;
        when(mockUser.getBalance()).thenReturn(balance);
        double result = atm.checkBalance();
        assertEquals(balance, result, "The balance returned should match the user's balance.");
        verify(mockUser).getBalance();
    }

    @Test
    @DisplayName("test deposit")
    public void testDeposit() {
        double depositAmount = 300.0;
        atm.deposit(depositAmount);
        verify(mockUser).deposit(depositAmount);
    }

    @Test
    @DisplayName("test when trying to withdraw money within  the range of mooney owned")
    public void testWithdrawWithinTheRangeOfAmountOwned() {
        double withdrawAmount = 200.0;
        double initialBalance = 1200.0;
        when(mockUser.getBalance()).thenReturn(initialBalance);
        boolean result = atm.withdraw(withdrawAmount);
        assertTrue(result, "The withdrawal should succeed if balance is sufficient.");
        verify(mockUser).getBalance();
        verify(mockUser).withdraw(withdrawAmount);
    }

    @Test
    @DisplayName("Test withdrawal of exact balance amount")
    public void testWithdrawExactBalanceAmount() {
        double initialBalance = 1200.0;
        double withdrawAmount = initialBalance;

        when(mockUser.getBalance()).thenReturn(initialBalance);

        boolean result = atm.withdraw(withdrawAmount);

        assertTrue(result, "The withdrawal should succeed when withdrawing the exact balance.");
        verify(mockUser).getBalance();
        verify(mockUser).withdraw(withdrawAmount);
    }

    @Test
    @DisplayName("test when trying to withdraw money outside the range of money owned ")
    public void testWithdrawAboveTheAmountOwned() {
        double withdrawAmount = 1500.0;
        double initialBalance = 1000.0;
        when(mockUser.getBalance()).thenReturn(initialBalance);
        boolean result = atm.withdraw(withdrawAmount);
        assertFalse(result, "The withdrawal should fail if balance is insufficient.");
        verify(mockUser).getBalance();
        verify(mockUser, never()).withdraw(withdrawAmount);
    }

    @Test
    @DisplayName("test main method card insert")
    public void testMainCardInsertAndPinSuccess() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        String simulatedInput = "user123\n5678\n4\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        ATM.main(new String[]{});

        System.setOut(originalOut);

        String output = outContent.toString();

        assertTrue(output.contains("insert your card (Enter user ID or type exit to quit the application):"));
        assertTrue(output.contains("Enter your PIN:"));
        assertTrue(output.contains("PIN accepted. Access granted."));
        assertTrue(output.contains("Select an option: 1-Withdraw || 2-Deposit || 3-Check Balance || 4- Exit"));
        assertTrue(output.contains("Exiting."));
    }


    @Test
    @DisplayName("test main method despoit ")
    public void testMainDepositSuccess() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream outPrint = System.out;
        System.setOut(new PrintStream(outContent));


        String simulatedInput = "user123\n5678\n2\n500\n3\n4\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));


        ATM.main(new String[]{});


        System.setOut(outPrint);


        String output = outContent.toString();


        assertTrue(output.contains("Deposit successful. New balance: 1500.0"));
        assertTrue(output.contains("Exiting."));
    }
    @Test
    @DisplayName("Test card locking after three failed PIN attempts")
    void testCardLockAfterThreeFailedPinAttempts() {
        when(mockUser.getId()).thenReturn("user123");
        when(mockUser.getPin()).thenReturn("5678");
        when(mockUser.isLocked()).thenReturn(false);

        when(mockBankInterface.getUserById("user123")).thenReturn(mockUser);
        when(mockUser.isLocked()).thenReturn(false);

        when(mockUser.getFailedAttempts())
                .thenReturn(1)
                .thenReturn(2)
                .thenReturn(3);

        atm.enterPin("0000");
        verify(mockUser).incrementFailedAttempts();

        atm.enterPin("1111");
        verify(mockUser, times(2)).incrementFailedAttempts();

        atm.enterPin("2222");
        verify(mockUser, times(3)).incrementFailedAttempts();
        verify(mockUser).lockCard();

        when(mockUser.isLocked()).thenReturn(true);
        boolean result = atm.enterPin("5678");
        assertFalse(result, "After three failed attempts, the card should be locked and access denied.");
    }
}
