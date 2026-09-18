package com.portfolio.util;

import com.portfolio.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InputValidator Unit Tests")
public class InputValidatorTest {

    @Test
    @DisplayName("validatePositiveDouble - Valid positive double strings")
    void testValidatePositiveDoubleSuccess() throws InvalidInputException {
        assertEquals(100.50, InputValidator.validatePositiveDouble("100.50", "price"), 0.001);
        assertEquals(0.0001, InputValidator.validatePositiveDouble(" 0.0001 ", "quantity"), 0.00001);
        assertEquals(5000.0, InputValidator.validatePositiveDouble("5000", "nav"), 0.001);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-5.5", "-100", "abc", "", "   "})
    @DisplayName("validatePositiveDouble - Should throw InvalidInputException on zero, negative, or invalid text")
    void testValidatePositiveDoubleFailure(String input) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validatePositiveDouble(input, "testField"));
    }

    @Test
    @DisplayName("validatePositiveInt - Valid positive integer strings")
    void testValidatePositiveIntSuccess() throws InvalidInputException {
        assertEquals(1, InputValidator.validatePositiveInt("1", "id"));
        assertEquals(42, InputValidator.validatePositiveInt(" 42 ", "units"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-99", "12.34", "xyz", ""})
    @DisplayName("validatePositiveInt - Should throw InvalidInputException on invalid integer inputs")
    void testValidatePositiveIntFailure(String input) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validatePositiveInt(input, "id"));
    }

    @Test
    @DisplayName("validateDate - Valid ISO-8601 date")
    void testValidateDateSuccess() throws InvalidInputException {
        LocalDate date = InputValidator.validateDate("2024-03-15");
        assertEquals(LocalDate.of(2024, 3, 15), date);
    }

    @ParameterizedTest
    @ValueSource(strings = {"15-03-2024", "2024/03/15", "invalid-date", "2024-02-31", ""})
    @DisplayName("validateDate - Should throw InvalidInputException on non-ISO or invalid dates")
    void testValidateDateFailure(String input) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateDate(input));
    }

    @Test
    @DisplayName("validateNonEmpty - Valid trimmed non-empty string")
    void testValidateNonEmptySuccess() throws InvalidInputException {
        assertEquals("RELIANCE", InputValidator.validateNonEmpty("  RELIANCE  ", "symbol"));
        assertEquals("Bitcoin", InputValidator.validateNonEmpty("Bitcoin", "name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("validateNonEmpty - Should throw InvalidInputException on empty or whitespace strings")
    void testValidateNonEmptyFailure(String input) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateNonEmpty(input, "field"));
    }

    @Test
    @DisplayName("validateMenuChoice - In range choices")
    void testValidateMenuChoiceSuccess() throws InvalidInputException {
        assertEquals(1, InputValidator.validateMenuChoice("1", 0, 9));
        assertEquals(0, InputValidator.validateMenuChoice("0", 0, 9));
        assertEquals(9, InputValidator.validateMenuChoice("9", 0, 9));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "10", "abc", ""})
    @DisplayName("validateMenuChoice - Out of range or non-numeric choices throw exception")
    void testValidateMenuChoiceFailure(String input) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateMenuChoice(input, 0, 9));
    }
}
