package com.demo;

/**
 * Validates user input: emails, usernames, and passwords.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  SONARQUBE LEARNING GUIDE — What Sonar finds in this file   │
 * ├─────────────────┬───────────────────────────────────────────┤
 * │ Issue Type      │ Location & Explanation                    │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 🔒 Security     │ ADMIN_PASSWORD constant — hardcoded       │
 * │    Hotspot      │ credentials are a critical security risk. │
 * │                 │ Anyone who reads the source code (or a    │
 * │                 │ decompiled JAR) sees the password.        │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 🐛 Bug          │ isAdminPassword() uses == instead of      │
 * │                 │ .equals() to compare Strings.             │
 * │                 │ == compares object references (memory     │
 * │                 │ addresses), not string content.           │
 * │                 │ It accidentally works for string literals │
 * │                 │ (due to JVM interning) but FAILS for any  │
 * │                 │ String built at runtime.                  │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 🐛 Bug          │ isValidEmail() — if email is null, the    │
 * │                 │ call to email.contains() throws           │
 * │                 │ NullPointerException.                     │
 * ├─────────────────┼───────────────────────────────────────────┤
 * │ 📊 Coverage Gap │ maskPassword() — the branch for           │
 * │                 │ password.length() <= 2 is never tested.   │
 * │                 │ SonarCloud shows that line RED.           │
 * └─────────────────┴───────────────────────────────────────────┘
 */
public class UserValidator {

    // ⚠️ SECURITY HOTSPOT (SonarQube → Critical):
    //   Hardcoded credentials in source code are a OWASP Top-10 risk.
    //   If this repo is ever made public (or the JAR is decompiled),
    //   the password is exposed.
    //
    //   THE FIX: Load from environment variable or secrets vault:
    //     private static final String ADMIN_PASSWORD =
    //         System.getenv("ADMIN_PASSWORD");
    private static final String ADMIN_PASSWORD = "admin123";

    // Named constants replace magic numbers → better readability
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;

    /**
     * Checks whether the given string is a valid email address.
     *
     * @param email the email address to check
     * @return true if the email contains '@' and '.'
     *
     * ⚠️ BUG (SonarQube → Blocker):
     *   No null-check on `email`.
     *   Calling email.contains("@") when email is null throws:
     *     java.lang.NullPointerException
     *
     *   THE FIX:
     *     if (email == null) return false;
     */
    public boolean isValidEmail(String email) {
        // ⚠️ BUG: Missing null check — NPE if email is null
        return email.contains("@") && email.contains(".");
    }

    /**
     * Checks whether the supplied password matches the admin password.
     *
     * @param password the password to check
     * @return true if it matches the admin password
     *
     * ⚠️ BUG (SonarQube → Critical):
     *   Uses == (reference equality) instead of .equals() (value equality).
     *
     *   WHY THIS IS A BUG:
     *     String literal  "admin123"  IS interned by the JVM → same reference → == is true  ✅
     *     new String("admin123")      is a NEW object         → == is false   ❌ (but value matches!)
     *
     *   UserValidatorTest.testAdminPasswordBug() PROVES this.
     *
     *   THE FIX:
     *     return ADMIN_PASSWORD.equals(password);
     */
    public boolean isAdminPassword(String password) {
        // ⚠️ BUG: == compares references, not string content
        return password == ADMIN_PASSWORD;
    }

    /**
     * Validates a username against the following rules:
     * <ul>
     *   <li>Not null or empty</li>
     *   <li>Between 3 and 20 characters</li>
     *   <li>Contains only letters and digits (no special characters)</li>
     * </ul>
     *
     * @param username the username to validate
     * @return true if the username is valid
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return username.length() >= MIN_USERNAME_LENGTH
                && username.length() <= MAX_USERNAME_LENGTH
                && username.matches("[a-zA-Z0-9]+");
    }

    /**
     * Returns a masked version of the password for safe display.
     * Only the last 2 characters are visible; the rest are replaced with *.
     *
     * <pre>
     *   maskPassword("admin123")  →  "******23"
     *   maskPassword(null)        →  "****"
     *   maskPassword("hi")        →  "**"   ← this branch is NOT tested
     * </pre>
     *
     * @param password the password to mask
     * @return the masked password string
     *
     * ⚠️ COVERAGE GAP:
     *   The `password.length() <= 2` branch is never hit by any test.
     *   SonarCloud shows it as a RED (uncovered) line.
     *   This means if someone passes a 1 or 2-character password,
     *   the behaviour is untested — a reliability risk.
     */
    public String maskPassword(String password) {
        if (password == null) {
            return "****";
        }
        if (password.length() <= 2) {
            // ❌ This branch is NOT tested → appears RED in SonarCloud
            return "**";
        }
        // ✅ This branch IS tested → appears GREEN in SonarCloud
        return "*".repeat(password.length() - 2)
                + password.substring(password.length() - 2);
    }
}

