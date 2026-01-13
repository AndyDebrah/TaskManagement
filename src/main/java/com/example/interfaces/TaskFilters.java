//
//package main.java.com.example.interfaces;
//
//import main.java.com.example.models.Task;
//import main.java.com.example.utils.FunctionalUtils;
//
//import java.time.LocalDate;
//import java.util.Locale;
//import java.util.regex.Pattern;
//
//public final class TaskFilters {
//
//    private TaskFilters() {}
//
//    public static TaskFilter completed() {
//        return TaskFilter.of(FunctionalUtils.isCompletedTask());
//    }
//
//    public static TaskFilter status(String status) {
//        return TaskFilter.of(FunctionalUtils.hasStatus(status));
//    }
//
//    public static TaskFilter priority(String priority) {
//        return TaskFilter.of(FunctionalUtils.hasPriority(priority));
//    }
//
//    public static TaskFilter assignedTo(String assignee) {
//        return TaskFilter.of(FunctionalUtils.assignedTo(assignee));
//    }
//
//    public static TaskFilter nameContains(String token) {
//        return TaskFilter.of(FunctionalUtils.nameContainsIgnoreCase(token));
//    }
//
//    public static TaskFilter validId() {
//        return TaskFilter.of(FunctionalUtils.hasValidId());
//    }
//
//    public static TaskFilter dueOnOrBefore(LocalDate cutoff) {
//        return TaskFilter.of(FunctionalUtils.dueOnOrBefore(cutoff));
//    }
//
//    public static TaskFilter dueAfter(LocalDate cutoff) {
//        return TaskFilter.of(FunctionalUtils.dueAfter(cutoff));
//    }
//
//    // Example: multiple statuses at once (e.g., "Pending" or "In Progress")
//    public static TaskFilter statusIn(String... statuses) {
//        return task -> {
//            if (task == null || task.getStatus() == null) return false;
//            String s = task.getStatus().toLowerCase(Locale.ROOT);
//            for (String allowed : statuses) {
//                if (allowed != null && s.equals(allowed.toLowerCase(Locale.ROOT))) return true;
//            }
//            return false;
//        };
//    }
//
//    // Example: match ID against custom pattern
//    public static TaskFilter idMatches(Pattern p) {
//        return task -> task != null && task.getTaskId() != null && p.matcher(task.getTaskId()).matches();
//    }
//}
//
