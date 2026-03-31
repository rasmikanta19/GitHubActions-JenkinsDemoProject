package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringUtils}.
 */
@DisplayName("StringUtils Tests")
class StringUtilsTest {

    private StringUtils stringUtils;

    @BeforeEach
    void setUp() {
        stringUtils = new StringUtils();
    }

    // -------------------------------------------------------------------------
    // reverse()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("reverse: regular string")
    void testReverseRegularString() {
        assertEquals("olleH", stringUtils.reverse("Hello"));
    }

    @Test
    @DisplayName("reverse: single character")
    void testReverseSingleCharacter() {
        assertEquals("a", stringUtils.reverse("a"));
    }

    @Test
    @DisplayName("reverse: null returns empty string")
    void testReverseNull() {
        assertEquals("", stringUtils.reverse(null));
    }

    @Test
    @DisplayName("reverse: empty string")
    void testReverseEmpty() {
        assertEquals("", stringUtils.reverse(""));
    }

    // -------------------------------------------------------------------------
    // isPalindrome()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("isPalindrome: classic palindrome word")
    void testIsPalindromeClassicWord() {
        assertTrue(stringUtils.isPalindrome("racecar"));
    }

    @Test
    @DisplayName("isPalindrome: sentence with spaces and mixed case")
    void testIsPalindromeSentence() {
        assertTrue(stringUtils.isPalindrome("A man a plan a canal Panama"));
    }

    @Test
    @DisplayName("isPalindrome: non-palindrome")
    void testIsNotPalindrome() {
        assertFalse(stringUtils.isPalindrome("hello"));
    }

    @Test
    @DisplayName("isPalindrome: null returns false")
    void testIsPalindromeNull() {
        assertFalse(stringUtils.isPalindrome(null));
    }

    // -------------------------------------------------------------------------
    // countWords()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("countWords: simple sentence")
    void testCountWordsSimpleSentence() {
        assertEquals(4, stringUtils.countWords("GitHub Actions vs Jenkins"));
    }

    @Test
    @DisplayName("countWords: extra whitespace between words")
    void testCountWordsExtraWhitespace() {
        assertEquals(3, stringUtils.countWords("  hello   world  test  "));
    }

    @Test
    @DisplayName("countWords: single word")
    void testCountWordsSingleWord() {
        assertEquals(1, stringUtils.countWords("Maven"));
    }

    @Test
    @DisplayName("countWords: null returns 0")
    void testCountWordsNull() {
        assertEquals(0, stringUtils.countWords(null));
    }

    @Test
    @DisplayName("countWords: blank string returns 0")
    void testCountWordsBlank() {
        assertEquals(0, stringUtils.countWords("   "));
    }

    // -------------------------------------------------------------------------
    // toTitleCase()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("toTitleCase: all lowercase")
    void testToTitleCaseAllLowercase() {
        assertEquals("Hello World", stringUtils.toTitleCase("hello world"));
    }

    @Test
    @DisplayName("toTitleCase: already capitalised")
    void testToTitleCaseAlreadyCapitalised() {
        assertEquals("Github Actions", stringUtils.toTitleCase("GITHUB ACTIONS"));
    }

    @Test
    @DisplayName("toTitleCase: null returns empty string")
    void testToTitleCaseNull() {
        assertEquals("", stringUtils.toTitleCase(null));
    }

    @Test
    @DisplayName("toTitleCase: empty string")
    void testToTitleCaseEmpty() {
        assertEquals("", stringUtils.toTitleCase(""));
    }

    @Test
    @DisplayName("Concat 2 Strings")
    void testConcat2Strings() {assertEquals("JaiJagannath",stringUtils.concatStrings("Jai","Jagannath"));}
}
