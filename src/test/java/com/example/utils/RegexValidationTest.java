package com.example.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RegexValidationTest {

    @Test
    void acceptsWeek3ProjectId() {
        assertTrue(RegexValidator.isValidProjectId("P001"));
        assertFalse(RegexValidator.isValidProjectId("P01")); // too short
    }

    @Test
    void acceptsLegacyProjectId() {
        assertTrue(RegexValidator.isValidProjectId("PRJ0001"));
        assertFalse(RegexValidator.isValidProjectId("PRJ01"));
    }

    @Test
    void acceptsWeek3TaskId() {
        assertTrue(RegexValidator.isValidTaskId("T123"));
        assertFalse(RegexValidator.isValidTaskId("T12A"));
    }

    @Test
    void acceptsLegacyTaskId() {
        assertTrue(RegexValidator.isValidTaskId("TSK0004"));
        assertFalse(RegexValidator.isValidTaskId("TSK4"));
    }
}

