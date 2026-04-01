package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link UserValidator}.
 *
 * ┌──────────────────────────────────────────────────────────────────┐
 * │  SONARQUBE LEARNING GUIDE — How these tests map to Sonar output  │
 * ├──────────────────────────┬───────────────────────────────────────┤
 * │ Test method              │ What it demonstrates in Sonar         │
 * ├──────────────────────────┼───────────────────────────────────────┤
 * │ testNullEmailThrowsNPE   │ PROVES the Bug: isValidEmail() has no │
 * │                          │ null-check → NPE at runtime           │
 * ├──────────────────────────┼───────────────────────────────────────┤
 * │ testAdminPasswordBug     │ PROVES the == Bug: runtime String     │
 * │                          │ object fails == even when value       │
 * │                          │ matches "admin123"                    │
 * ├──────────────────────────┼───────────────────────────────────────┤
 * │ testAdminPasswordLiteral │ Shows == accidentally works for       │
 * │                          │ interned literals (masks the bug)     │
 * ├──────────────────────────┼───────────────────────────────────────┤
 * │ (missing test)           │ maskPassword short-string branch is   │
 * │                          │ NOT tested → RED line on Sonar        │
 * └──────────────────────────┴───────────────────────────────────────┘
 *
 * COVERAGE SUMMARY FOR THIS CLASS (what SonarCloud will show):
 *
 *   Method                 Covered?   Notes
 *   ─────────────────────  ─────────  ─────────────────────────────
 *   isValidEmail()         ✅ Yes     valid, missing-@, null (NPE)
 *   isAdminPassword()      ✅ Yes     runtime string + literal
 *   isValidUsername()      ✅ Yes     valid, null, too-short, too-long, special chars
 *   maskPassword()         ⚠️ Partial null + normal, but NOT length<=2 branch ❌
 */
@DisplayName("UserValidator Tests")
class UserValidatorTest {

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator();
    }

    // ─────────────────────────────────────────────────────────────────
    // isValidEmail() tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isValidEmail: valid email returns true")
    void testValidEmail() {
        assertTrue(validator.isValidEmail("user@example.com"),
                "A well-formed email should return true");
    }

    @Test
    @DisplayName("isValidEmail: missing '@' returns false")
    void testEmailMissingAtSign() {
        assertFalse(validator.isValidEmail("userexample.com"),
                "Email without '@' should return false");
    }

    @Test
    @DisplayName("isValidEmail: missing '.' returns false")
    void testEmailMissingDot() {
        assertFalse(validator.isValidEmail("user@examplecom"),
                "Email without '.' should return false");
    }

    /**
     * ⚠️ BUG DEMONSTRATION TEST
     *
     * This test PROVES the null-pointer bug that SonarQube flags
     * in isValidEmail().
     *
     * The source code does:
     *   return email.contains("@") && email.contains(".");
     *
     * When email is null, Java throws:
     *   java.lang.NullPointerException: Cannot invoke
     *   "String.contains(CharSequence)" because "email" is null
     *
     * SonarQube sees the missing null guard and raises a Bug issue
     * pointing to the `email.contains(...)` line.
     *
     * THE FIX:
     *   if (email == null) return false;
     */
    @Test
    @DisplayName("isValidEmail: BUG PROOF — null input causes NullPointerException")
    void testNullEmailThrowsNPE() {
        assertThrows(NullPointerException.class,
                () -> validator.isValidEmail(null),
                "BUG CONFIRMED: null email causes NPE because isValidEmail() "
                + "has no null guard");
    }

    // ─────────────────────────────────────────────────────────────────
    // isAdminPassword() tests
    // ─────────────────────────────────────────────────────────────────

    /**
     * ⚠️ BUG DEMONSTRATION TEST  (the sneaky == vs .equals() bug)
     *
     * HOW JAVA STRING COMPARISON WORKS:
     *
     *   ==         compares REFERENCES (memory addresses)
     *   .equals()  compares VALUES     (the actual characters)
     *
     * String INTERNING:
     *   Java automatically places string literals in a special pool.
     *   "admin123" written in source code always points to the SAME
     *   object in that pool → == accidentally returns true.
     *
     *   new String("admin123") creates a BRAND NEW object on the heap
     *   with a DIFFERENT memory address → == returns false, even
     *   though the character content is identical.
     *
     * This bug is nearly IMPOSSIBLE to catch by manual code review
     * because unit tests using string literals will pass.
     * SonarQube's static analysis catches it by inspecting the bytecode.
     */
    @Test
    @DisplayName("isAdminPassword: BUG PROOF — runtime String fails == even with correct value")
    void testAdminPasswordBugWithRuntimeString() {
        // Force a new String object (NOT interned) with the correct value
        String runtimePassword = new String("admin123");

        // ⚠️ BUG: == compares references → false, even though value is correct!
        boolean result = validator.isAdminPassword(runtimePassword);

        assertFalse(result,
                "BUG CONFIRMED: isAdminPassword returns false for a runtime "
                + "String with value 'admin123' because == compares references, "
                + "not values. The fix is to use .equals() instead.");
    }

    @Test
    @DisplayName("isAdminPassword: string literal works accidentally (masks the bug)")
    void testAdminPasswordWithLiteralMasksTheBug() {
        // String literals ARE interned → same reference → == accidentally works
        // This is why the bug goes unnoticed in many codebases!
        assertTrue(validator.isAdminPassword("admin123"),
                "String literals are interned by JVM so == works here — "
                + "this masks the bug and makes it hard to spot without Sonar");
    }

    // ─────────────────────────────────────────────────────────────────
    // isValidUsername() tests
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isValidUsername: valid alphanumeric username returns true")
    void testValidUsername() {
        assertTrue(validator.isValidUsername("john123"),
                "A valid alphanumeric username should return true");
    }

    @Test
    @DisplayName("isValidUsername: null returns false")
    void testNullUsernameReturnsFalse() {
        assertFalse(validator.isValidUsername(null),
                "Null username should return false");
    }

    @Test
    @DisplayName("isValidUsername: empty string returns false")
    void testEmptyUsernameReturnsFalse() {
        assertFalse(validator.isValidUsername(""),
                "Empty username should return false");
    }

    @Test
    @DisplayName("isValidUsername: too short (< 3 chars) returns false")
    void testTooShortUsername() {
        assertFalse(validator.isValidUsername("ab"),
                "Username shorter than 3 characters should return false");
    }

    @Test
    @DisplayName("isValidUsername: too long (> 20 chars) returns false")
    void testTooLongUsername() {
        assertFalse(validator.isValidUsername("thisusernameiswaytoolong"),
                "Username longer than 20 characters should return false");
    }

    @Test
    @DisplayName("isValidUsername: special characters return false")
    void testUsernameWithSpecialChars() {
        assertFalse(validator.isValidUsername("user@name"),
                "Username with '@' should return false");
        assertFalse(validator.isValidUsername("user name"),
                "Username with space should return false");
    }

    // ─────────────────────────────────────────────────────────────────
    // maskPassword() tests — INTENTIONAL COVERAGE GAP
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("maskPassword: null returns '****'")
    void testMaskPasswordNull() {
        assertEquals("****", validator.maskPassword(null),
                "Null password should be masked as '****'");
    }

    @Test
    @DisplayName("maskPassword: normal password shows only last 2 characters")
    void testMaskPasswordNormal() {
        // "admin123" → 8 chars → 6 stars + last 2 chars "23"
        assertEquals("******23", validator.maskPassword("admin123"),
                "Password 'admin123' should be masked as '******23'");
    }

    @Test
    @DisplayName("maskPassword: 3-char password shows only last 2 characters")
    void testMaskPasswordShortButAbove2() {
        // "abc" → 3 chars → 1 star + last 2 chars "bc"
        assertEquals("*bc", validator.maskPassword("abc"),
                "Password 'abc' should be masked as '*bc'");
    }

    /**
     * ⚠️ INTENTIONAL COVERAGE GAP
     *
     * We intentionally do NOT test the `password.length() <= 2` branch
     * in maskPassword().
     *
     * A test for it would look like:
     *   assertEquals("**", validator.maskPassword("hi"));
     *
     * Without this test, SonarCloud shows the line:
     *   return "**";
     * in RED on the coverage report.
     *
     * This teaches you that RED lines in SonarCloud mean:
     *   "Your tests never executed this code path — if there's a bug
     *    here, you would never know until it hits production."
     */
    // testMaskPasswordLengthTwoOrLess() is intentionally MISSING
    // → triggers the RED coverage gap on SonarCloud
}

