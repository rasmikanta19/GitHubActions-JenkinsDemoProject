package com.demo;

/**
 * A simple bank account class.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  SONARQUBE LEARNING GUIDE — What Sonar finds in this file   │
 * ├─────────────────┬───────────────────────────────────────────┤
 * │ Issue Type      │ Location & Explanation                    │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 🐛 Bug          │ withdraw() — does not check that the      │
 * │                 │ amount is ≤ current balance, so balance   │
 * │                 │ silently goes negative.                   │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 🐛 Bug          │ Constructor — no null-check on owner;     │
 * │                 │ getOwner().length() later throws NPE.     │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 😷 Code Smell   │ getAccountSummary() uses a magic number   │
 * │                 │ (0) as a boundary — should be a named     │
 * │                 │ constant (ZERO_BALANCE = 0).              │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 📊 Coverage Gap │ getAccountSummary() has 3 branches:       │
 * │                 │   balance > 0   ← tested ✅               │
 * │                 │   balance == 0  ← NOT tested ❌           │
 * │                 │   balance < 0   ← NOT tested ❌           │
 * │                 │ SonarCloud shows those lines in RED.      │
 * └─────────────────┴───────────────────────────────────────────┘
 */
public class BankAccount {

    private final String owner;
    private double balance;

    // Maximum number of transactions allowed per account session
    private static final int MAX_TRANSACTIONS = 100;
    private int transactionCount = 0;

    /**
     * Creates a new BankAccount.
     *
     * @param owner          the account holder's name
     * @param initialBalance the starting balance
     *
     * ⚠️ BUG (SonarQube → Blocker):
     *   No null-check on `owner`.
     *   Any code that later calls getOwner().toUpperCase() will
     *   throw a NullPointerException if owner was passed as null.
     */
    public BankAccount(String owner, double initialBalance) {
        // SonarQube flags: "owner" is used without null check
        this.owner = owner;
        this.balance = initialBalance;
    }

    /**
     * Deposits a positive amount into the account.
     *
     * @param amount the amount to deposit (must be &gt; 0)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        transactionCount++;
    }

    /**
     * Withdraws the specified amount from the account.
     *
     * @param amount the amount to withdraw (must be &gt; 0)
     * @throws IllegalArgumentException if amount is not positive
     *
     * ⚠️ BUG (SonarQube → Critical):
     *   There is NO check that balance >= amount before subtracting.
     *   If a user withdraws more than their balance, the balance
     *   silently becomes NEGATIVE — a business logic bug.
     *
     *   The FIX would be to add:
     *     if (amount > balance) {
     *         throw new IllegalArgumentException("Insufficient funds.");
     *     }
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        // ⚠️ BUG: Missing check → balance can go negative
        balance -= amount;
        transactionCount++;
    }

    /**
     * Returns the current balance.
     *
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Returns the account owner's name.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns whether the maximum transaction limit has been reached.
     *
     * @return true if the transaction limit is reached
     */
    public boolean hasReachedTransactionLimit() {
        return transactionCount >= MAX_TRANSACTIONS;
    }

    /**
     * Returns a human-readable summary of the account balance.
     *
     * ⚠️ CODE SMELL (SonarQube → Minor):
     *   The literal 0 is a "magic number". It should be extracted
     *   to a named constant so the intent is self-documenting:
     *     private static final double ZERO_BALANCE = 0.0;
     *
     * ⚠️ COVERAGE GAP:
     *   BankAccountTest only tests the `balance > 0` branch.
     *   The `balance == 0` and `balance < 0` branches are
     *   NOT covered by any test.
     *   In the SonarCloud dashboard, those lines appear RED
     *   (= uncovered), which lowers the overall coverage %.
     */
    public String getAccountSummary() {
        if (balance > 0) {
            // ✅ This branch IS tested → appears GREEN in coverage report
            return owner + " has a balance of £" + balance + ".";
        } else if (balance == 0) {
            // ❌ This branch is NOT tested → appears RED in coverage report
            return owner + " has an empty account.";
        } else {
            // ❌ This branch is NOT tested → appears RED in coverage report
            return owner + " is overdrawn by £" + Math.abs(balance) + ".";
        }
    }
}

