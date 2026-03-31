package com.demo;

/**
 * Utility class providing common String manipulation operations.
 *
 * <p>This class is used alongside {@link Calculator} to demonstrate unit
 * testing with JUnit 5 and CI/CD pipelines with both GitHub Actions and Jenkins.</p>
 */
public class StringUtils {

    /**
     * Reverses the characters in the given string.
     *
     * @param input the string to reverse
     * @return the reversed string, or an empty string if input is null
     */
    public String reverse(String input) {
        if (input == null) {
            return "";
        }
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * Checks whether the given string is a palindrome (case-insensitive).
     *
     * @param input the string to check
     * @return {@code true} if input reads the same forwards and backwards,
     *         {@code false} otherwise
     */
    public boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String cleaned = input.toLowerCase().replaceAll("[^a-z0-9]", "");
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    /**
     * Counts the number of words in the given string.
     * Words are separated by one or more whitespace characters.
     *
     * @param input the string to count words in
     * @return the number of words, or 0 if the string is null or blank
     */
    public int countWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }
        return input.trim().split("\\s+").length;
    }

    /**
     * Capitalises the first letter of each word in the given string.
     *
     * @param input the string to capitalise
     * @return the title-cased string, or an empty string if input is null
     */
    public String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String[] words = input.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }
    public String concatStrings(String first, String second) {
        return first + second;
    }
}
