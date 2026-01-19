package com.example.utils;

import com.example.models.Task;

import java.util.List;
import java.util.Objects;

/**
 * Small utility to calculate completion percentage for an array or list of tasks.
 */
public class CompletionCalculator {

    /**
     * Calculate completion percentage for an array of tasks.
     * Returns 0.0 for null or empty arrays. Result is in range [0.0, 100.0].
     */
    public static double calculateCompletion(Task[] tasks) {
        if (tasks == null || tasks.length == 0) return 0.0;
        int total = tasks.length;
        int completed = 0;
        for (Task t : tasks) {
            if (t != null && t.isCompleted()) completed++;
        }
        return ((double) completed / total) * 100.0;
    }

    /**
     * Calculate completion percentage for a list of tasks.
     * Returns 0.0 for null or empty lists. Result is in range [0.0, 100.0].
     */
    public static double calculateCompletion(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0.0;
        return calculateCompletion(tasks.stream().filter(Objects::nonNull).toArray(Task[]::new));
    }
}

