package com.example.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.exceptions.InvalidInputException;

class ValidationTest {

    @Test
    void isValidString_checks() {
        assertTrue(ValidationUtils.isValidString(null));
        assertTrue(ValidationUtils.isValidString(""));
        assertTrue(ValidationUtils.isValidString("   "));
        assertFalse(ValidationUtils.isValidString("abc"));
    }

    @Test
    void requireNonEmpty_throws_on_invalid() {
        assertThrows(InvalidInputException.class, () -> ValidationUtils.requireNonEmpty(null, "Name"));
        assertThrows(InvalidInputException.class, () -> ValidationUtils.requireNonEmpty("   ", "Name"));
    }

    @Test
    void requireValidPriority_and_status() {
        assertThrows(InvalidInputException.class, () -> ValidationUtils.requireValidPriority("Bad"));
        assertDoesNotThrow(() -> ValidationUtils.requireValidPriority("High"));

        assertThrows(InvalidInputException.class, () -> ValidationUtils.requireValidStatus("Unknown"));
        assertDoesNotThrow(() -> ValidationUtils.requireValidStatus("Active"));
    }
}
