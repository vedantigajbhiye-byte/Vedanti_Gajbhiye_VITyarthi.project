package com.portfolio.exception;

/**
 * Custom exception for invalid user input.
 * Demonstrates: Java Exception Handling, Custom Exceptions (Unit 3)
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String field, String reason) {
        super("Invalid value for '" + field + "': " + reason);
    }
}
