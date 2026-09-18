package com.portfolio.util;

import com.portfolio.exception.InvalidInputException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Utility class for validating user inputs.
 * Demonstrates: Static methods, Exception Handling (Units 2, 3)
 */
public class InputValidator {

    // Private constructor - utility class should not be instantiated
    private InputValidator() {}

    public static double validatePositiveDouble(String value, String fieldName) throws InvalidInputException {
        try {
            double parsed = Double.parseDouble(value.trim());
            if (parsed <= 0) {
                throw new InvalidInputException(fieldName, "must be a positive number (got: " + value + ")");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName, "must be a valid number (got: '" + value + "')");
        }
    }

    public static int validatePositiveInt(String value, String fieldName) throws InvalidInputException {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new InvalidInputException(fieldName, "must be a positive integer (got: " + value + ")");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName, "must be a valid integer (got: '" + value + "')");
        }
    }

    public static LocalDate validateDate(String value) throws InvalidInputException {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("date", "must be in YYYY-MM-DD format (got: '" + value + "')");
        }
    }

    public static String validateNonEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName, "cannot be empty");
        }
        return value.trim();
    }

    public static int validateMenuChoice(String value, int min, int max) throws InvalidInputException {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < min || parsed > max) {
                throw new InvalidInputException("choice", "must be between " + min + " and " + max);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("choice", "must be a number (got: '" + value + "')");
        }
    }
}
