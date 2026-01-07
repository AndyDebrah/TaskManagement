package test.java.com.example.utils;

import main.java.com.example.exceptions.InvalidInputException;
import main.java.com.example.utils.ValidationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
