package com.demo;

/**
 * A simple Calculator class that provides basic arithmetic operations.
 *
 * <p>This class is used to demonstrate unit testing with JUnit 5 and
 * CI/CD pipelines with both GitHub Actions and Jenkins.</p>
 */
public class Calculator {

    /**
     * Adds two integers and returns the result.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the sum of a and b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Subtracts the second integer from the first.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the result of a minus b
     */
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * Multiplies two integers and returns the result.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the product of a and b
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Divides the first integer by the second.
     *
     * @param a the dividend
     * @param b the divisor (must not be zero)
     * @return the result of a divided by b
     * @throws IllegalArgumentException if b is zero
     */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor must not be zero.");
        }
        return (double) a / b;
    }

    /**
     * Returns the factorial of a non-negative integer.
     *
     * @param n a non-negative integer
     * @return n! (n factorial)
     * @throws IllegalArgumentException if n is negative
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Input must be a non-negative integer.");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
