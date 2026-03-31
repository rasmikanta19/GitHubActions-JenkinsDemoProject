package com.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Calculator}.
 */
@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    // -------------------------------------------------------------------------
    // add()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("add: positive numbers")
    void testAddPositiveNumbers() {
        assertEquals(10, calculator.add(4, 6));
    }

    @Test
    @DisplayName("add: negative numbers")
    void testAddNegativeNumbers() {
        assertEquals(-9, calculator.add(-4, -5));
    }

    @Test
    @DisplayName("add: mixed sign numbers")
    void testAddMixedNumbers() {
        assertEquals(1, calculator.add(5, -4));
    }

    // -------------------------------------------------------------------------
    // subtract()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("subtract: positive result")
    void testSubtractPositiveResult() {
        assertEquals(3, calculator.subtract(7, 4));
    }

    @Test
    @DisplayName("subtract: negative result")
    void testSubtractNegativeResult() {
        assertEquals(-3, calculator.subtract(4, 7));
    }

    // -------------------------------------------------------------------------
    // multiply()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("multiply: two positive numbers")
    void testMultiplyPositiveNumbers() {
        assertEquals(20, calculator.multiply(4, 5));
    }

    @Test
    @DisplayName("multiply: by zero")
    void testMultiplyByZero() {
        assertEquals(0, calculator.multiply(100, 0));
    }

    @Test
    @DisplayName("multiply: negative numbers")
    void testMultiplyNegativeNumbers() {
        assertEquals(20, calculator.multiply(-4, -5));
    }

    // -------------------------------------------------------------------------
    // divide()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("divide: exact division")
    void testDivideExact() {
        assertEquals(5.0, calculator.divide(10, 2));
    }

    @Test
    @DisplayName("divide: decimal result")
    void testDivideDecimalResult() {
        assertEquals(2.5, calculator.divide(5, 2));
    }

    @Test
    @DisplayName("divide: by zero throws exception")
    void testDivideByZeroThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.divide(10, 0)
        );
        assertEquals("Divisor must not be zero.", ex.getMessage());
    }

    // -------------------------------------------------------------------------
    // factorial()
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("factorial: of 0 is 1")
    void testFactorialOfZero() {
        assertEquals(1, calculator.factorial(0));
    }

    @Test
    @DisplayName("factorial: of 5 is 120")
    void testFactorialOfFive() {
        assertEquals(120, calculator.factorial(5));
    }

    @Test
    @DisplayName("factorial: negative input throws exception")
    void testFactorialNegativeInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-1));
    }
}
