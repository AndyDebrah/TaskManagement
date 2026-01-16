
package main.java.com.example.interfaces;

import main.java.com.example.models.Task;
import main.java.com.example.utils.FunctionalUtils;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Common TaskFilter implementations for convenience.
 */
public final class TaskFilters {

    private TaskFilters() {}

    public static TaskFilter completed() {
        return task -> FunctionalUtils.isCompletedTask().test(task);
    }

    public static TaskFilter status(String status) {
        return task -> FunctionalUtils.hasStatus(status).test(task);
    }

    public static TaskFilter priority(String priority) {
        return task -> FunctionalUtils.hasPriority(priority).test(task);
    }

    public static TaskFilter assignedTo(String assignee) {
        return task -> FunctionalUtils.assignedTo(assignee).test(task);
    }

    public static TaskFilter nameContains(String token) {
        return task -> FunctionalUtils.nameContainsIgnoreCase(token).test(task);
    }

    public static TaskFilter validId() {
        return task -> FunctionalUtils.hasValidId().test(task);
    }

    public static TaskFilter dueOnOrBefore(LocalDate cutoff) {
        return task -> FunctionalUtils.dueOnOrBefore(cutoff).test(task);
    }

    public static TaskFilter dueAfter(LocalDate cutoff) {
        return task -> FunctionalUtils.dueAfter(cutoff).test(task);
    }

    public static TaskFilter statusIn(String... statuses) {
        return task -> {
            if (task == null || task.getStatus() == null) return false;
            String s = task.getStatus().toLowerCase(Locale.ROOT);
            for (String allowed : statuses) {
                if (allowed != null && s.equals(allowed.toLowerCase(Locale.ROOT))) return true;
            }
            return false;
        };
    }

    public static TaskFilter idMatches(Pattern p) {
        return task -> task != null && task.getTaskId() != null && p.matcher(task.getTaskId()).matches();
    }
}
