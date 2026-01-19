
package com.example.utils;

import java.util.Objects;
import java.util.regex.Pattern;

public final class RegexValidator {

    private RegexValidator() {}

    // Week 3 target patterns
    private static final Pattern PROJECT_ID_V3 = Pattern.compile("^P\\d{3}$");
    private static final Pattern TASK_ID_V3    = Pattern.compile("^T\\d{3}$");

    // Backward-compatible (existing seed/generators)
    private static final Pattern PROJECT_ID_LEGACY = Pattern.compile("^PRJ\\d{4}$");
    private static final Pattern TASK_ID_LEGACY    = Pattern.compile("^TSK\\d{4}$");

    public static boolean isValidProjectId(String projectId) {
        if (isBlank(projectId)) return false;
        return PROJECT_ID_V3.matcher(projectId).matches()
                || PROJECT_ID_LEGACY.matcher(projectId).matches();
    }

    public static boolean isValidTaskId(String taskId) {
        if (isBlank(taskId)) return false;
        return TASK_ID_V3.matcher(taskId).matches()
                || TASK_ID_LEGACY.matcher(taskId).matches();
    }

    // If later you want to enforce *only* V3, flip these helpers:
    public static boolean isStrictProjectId(String projectId) {
        return !isBlank(projectId) && PROJECT_ID_V3.matcher(projectId).matches();
    }

    public static boolean isStrictTaskId(String taskId) {
        return !isBlank(taskId) && TASK_ID_V3.matcher(taskId).matches();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

