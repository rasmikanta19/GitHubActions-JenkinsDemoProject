package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BankAccount}.
 *
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  SONARQUBE LEARNING GUIDE — How these tests map to Sonar output │
 * ├────────────────────────┬────────────────────────────────────────┤
 * │ Test method            │ What it demonstrates in Sonar          │
 * ├────────────────────────┼────────────────────────────────────────┤
 * │ testWithdrawBeyondBal  │ PROVES the Bug in withdraw():          │
 * │                        │ balance goes negative → Sonar flags it │
 * ├────────────────────────┼────────────────────────────────────────┤
 * │ testGetAccountSummary  │ Only covers the `balance > 0` branch.  │
 * │ PositiveBalance        │ The == 0 and < 0 branches stay RED     │
 * │                        │ (uncovered) on the Sonar dashboard.    │
 * ├────────────────────────┼────────────────────────────────────────┤
 * │ (missing tests)        │ No test for zero or negative balance   │
 * │                        │ → SonarCloud shows coverage < 100%    │
 * └────────────────────────┴────────────────────────────────────────┘
 *
 * COVERAGE SUMMARY FOR THIS CLASS (what SonarCloud will show):
 *
 *   Method                  Covered?   Notes
 *   ──────────────────────  ─────────  ─────────────────────────────
 *   BankAccount()           ✅ Yes
 *   deposit()               ✅ Yes     happy path + invalid amount
 *   withdraw()              ✅ Yes     happy path + invalid + OVER-draw
 *   getBalance()            ✅ Yes
 *   getOwner()              ✅ Yes
 *   hasReachedTransLimit()  ✅ Yes
 *   getAccountSummary()     ⚠️ Partial balance>0 only (2 branches ❌)
 */
@DisplayName("BankAccount Tests")
class BankAccountTest {

    private BankAccount account;

    @BeforeEach
    void setUp() {
        // Arrange: create a fresh account before each test
        account = new BankAccount("Alice", 1000.0);
    }

    // ─────────────────────────────────────────────────────────────────
    // Constructor tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Constructor: sets owner and initial balance correctly")
    void testConstructorSetsFields() {
        assertEquals("Alice", account.getOwner());
        assertEquals(1000.0, account.getBalance());
    }

    // ─────────────────────────────────────────────────────────────────
    // deposit() tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deposit: increases balance by the deposited amount")
    void testDepositIncreasesBalance() {
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance(),
                "Balance should be 1500 after depositing 500 into 1000");
    }

    @Test
    @DisplayName("deposit: zero amount throws IllegalArgumentException")
    void testDepositZeroThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(0),
                "Depositing zero should throw an exception");
    }

    @Test
    @DisplayName("deposit: negative amount throws IllegalArgumentException")
    void testDepositNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> account.deposit(-100.0),
                "Depositing a negative amount should throw an exception");
    }

    // ─────────────────────────────────────────────────────────────────
    // withdraw() tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("withdraw: decreases balance by the withdrawn amount")
    void testWithdrawDecreasesBalance() {
        account.withdraw(200.0);
        assertEquals(800.0, account.getBalance(),
                "Balance should be 800 after withdrawing 200 from 1000");
    }

    @Test
    @DisplayName("withdraw: zero amount throws IllegalArgumentException")
    void testWithdrawZeroThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(0),
                "Withdrawing zero should throw an exception");
    }

    @Test
    @DisplayName("withdraw: negative amount throws IllegalArgumentException")
    void testWithdrawNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> account.withdraw(-50.0),
                "Withdrawing a negative amount should throw an exception");
    }

    /**
     * ⚠️ BUG DEMONSTRATION TEST
     *
     * This test intentionally PROVES the bug that SonarQube flags
     * in withdraw(): there is no "insufficient funds" guard, so
     * the balance silently goes negative.
     *
     * When SonarQube analyzes BankAccount.java, it sees:
     *   balance -= amount;   ← no guard before this line
     * and raises a Bug issue pointing to that exact line.
     *
     * THE FIX (not applied here, intentionally):
     *   if (amount > balance) {
     *       throw new IllegalArgumentException("Insufficient funds.");
     *   }
     */
    @Test
    @DisplayName("withdraw: BUG PROOF — balance goes negative (no guard in withdraw)")
    void testWithdrawBeyondBalanceProvesBug() {
        // Withdraw MORE than the current balance of 1000
        account.withdraw(1500.0);

        // The bug: balance is now -500 (should have thrown an exception!)
        assertEquals(-500.0, account.getBalance(),
                "BUG CONFIRMED: balance went negative because withdraw() "
                + "has no insufficient-funds check");
    }

    // ─────────────────────────────────────────────────────────────────
    // hasReachedTransactionLimit() tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("hasReachedTransactionLimit: returns false on a fresh account")
    void testTransactionLimitNotReachedInitially() {
        assertFalse(account.hasReachedTransactionLimit(),
                "A new account should not have reached the transaction limit");
    }

    // ─────────────────────────────────────────────────────────────────
    // getAccountSummary() tests — INTENTIONAL COVERAGE GAP
    // ─────────────────────────────────────────────────────────────────

    /**
     * ⚠️ INTENTIONAL COVERAGE GAP
     *
     * We test ONLY the `balance > 0` branch.
     * The `balance == 0` and `balance < 0` branches have NO test.
     *
     * Open SonarCloud after the pipeline runs and navigate to:
     *   BankAccount.java → getAccountSummary()
     *
     * You will see:
     *   Line "return owner + ... £" + balance + "."   → GREEN (✅ covered)
     *   Line "return owner + ... empty account."      → RED   (❌ not covered)
     *   Line "return owner + ... overdrawn by £"      → RED   (❌ not covered)
     *
     * This teaches you what the RED lines in the coverage report mean.
     */
    @Test
    @DisplayName("getAccountSummary: positive balance branch — other branches left uncovered")
    void testGetAccountSummaryPositiveBalance() {
        String summary = account.getAccountSummary();

        assertTrue(summary.contains("Alice"),
                "Summary should contain the owner name");
        assertTrue(summary.contains("1000.0"),
                "Summary should contain the balance");

        // ⚠️ NOTE: We intentionally do NOT test:
        //   account = new BankAccount("Alice", 0.0);  ← empty account branch
        //   account = new BankAccount("Alice", -500); ← overdrawn branch
        // → SonarCloud shows those two branches as RED in the coverage report
    }
}

