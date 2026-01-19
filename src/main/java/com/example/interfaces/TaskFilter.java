
package com.example.interfaces;

import com.example.models.Task;

/**
 * Functional interface for task filtering.
 * Represents a single condition applied to a Task.
 */
@FunctionalInterface
public interface TaskFilter {
    boolean test(Task task);
}
