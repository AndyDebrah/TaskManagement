
package main.java.com.example.utils;

import main.java.com.example.models.Project;
import main.java.com.example.models.Task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Functional helper methods (predicates, mappers, comparators, collectors)
 * for Tasks and Projects. These utilities are null-safe and designed to compose
 * with Streams in services and tests.
 */
public final class FunctionalUtils {

    private FunctionalUtils() { }

    // ---------------------------------------------------------------------
    // Predicates (Task)
    // ---------------------------------------------------------------------

    /** Task is completed (status == "Completed"). */
    public static Predicate<Task> isCompletedTask() {
        return t -> t != null && t.isCompleted();
    }

    /** Task status equals (case-insensitive). */
    public static Predicate<Task> hasStatus(String status) {
        Objects.requireNonNull(status, "status");
        return t -> t != null && status.equalsIgnoreCase(t.getStatus());
    }

    /** Task priority equals (case-insensitive). */
    public static Predicate<Task> hasPriority(String priority) {
        Objects.requireNonNull(priority, "priority");
        return t -> t != null && priority.equalsIgnoreCase(t.getPriority());
    }

    /** Task assignedTo equals (case-insensitive). */
    public static Predicate<Task> assignedTo(String assignee) {
        Objects.requireNonNull(assignee, "assignee");
        return t -> t != null && assignee.equalsIgnoreCase(t.getAssignedTo());
    }

    /** Task name contains token (case-insensitive). */
    public static Predicate<Task> nameContainsIgnoreCase(String token) {
        Objects.requireNonNull(token, "token");
        String needle = token.toLowerCase(Locale.ROOT);
        return t -> t != null
                && t.getTaskName() != null
                && t.getTaskName().toLowerCase(Locale.ROOT).contains(needle);
    }

    /** Task ID passes RegexValidator (supports v3 + legacy). */
    public static Predicate<Task> hasValidId() {
        return t -> t != null && RegexValidator.isValidTaskId(t.getTaskId());
    }

    /** Task due date exists AND is on/before the given date (ISO yyyy-MM-dd). */
    public static Predicate<Task> dueOnOrBefore(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return t -> parseIsoDate(t == null ? null : t.getDueDate())
                .map(d -> !d.isAfter(date))
                .orElse(false);
    }

    /** Task due date exists AND is after the given date (ISO yyyy-MM-dd). */
    public static Predicate<Task> dueAfter(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return t -> parseIsoDate(t == null ? null : t.getDueDate())
                .map(d -> d.isAfter(date))
                .orElse(false);
    }

    // ---------------------------------------------------------------------
    // Predicates (Project)
    // ---------------------------------------------------------------------

    /** Project completion ≥ threshold (0..100). */
    public static Predicate<Project> completionAtLeast(double thresholdPercent) {
        return p -> p != null && p.calculateCompletionPercentage() >= thresholdPercent;
    }

    /** Project considered completed when completionPercentage >= 100.0. */
    public static Predicate<Project> isCompletedProject() {
        // Use calculateCompletionPercentage() for robustness across builds
        return p -> p != null && p.calculateCompletionPercentage() >= 100.0;
    }

    /** Project name contains token (case-insensitive). */
    public static Predicate<Project> projectNameContainsIgnoreCase(String token) {
        Objects.requireNonNull(token, "token");
        String needle = token.toLowerCase(Locale.ROOT);
        return p -> p != null
                && p.getProjectName() != null
                && p.getProjectName().toLowerCase(Locale.ROOT).contains(needle);
    }

    /** Project type equals (case-insensitive). */
    public static Predicate<Project> hasProjectType(String type) {
        Objects.requireNonNull(type, "type");
        return p -> p != null
                && p.getProjectType() != null
                && p.getProjectType().equalsIgnoreCase(type);
    }

    /** Project has any task that matches the given task predicate. */
    public static Predicate<Project> hasAnyTask(Predicate<Task> taskPredicate) {
        Objects.requireNonNull(taskPredicate, "taskPredicate");
        return p -> p != null && Arrays.stream(p.getTasks()).anyMatch(taskPredicate);
    }

    // ---------------------------------------------------------------------
    // Mappers
    // ---------------------------------------------------------------------

    public static Function<Task, String> toTaskId()      { return t -> t == null ? null : t.getTaskId(); }
    public static Function<Task, String> toTaskName()    { return t -> t == null ? null : t.getTaskName(); }
    public static Function<Task, String> toAssignee()    { return t -> t == null ? null : t.getAssignedTo(); }
    public static Function<Task, String> toPriority()    { return t -> t == null ? null : t.getPriority(); }
    public static Function<Task, String> toStatus()      { return t -> t == null ? null : t.getStatus(); }
    public static Function<Task, String> toDueDate()     { return t -> t == null ? null : t.getDueDate(); }

    public static Function<Project, String> toProjectId(){ return p -> p == null ? null : p.getProjectId(); }
    public static Function<Project, String> toProjectName(){ return p -> p == null ? null : p.getProjectName(); }
    public static ToDoubleFunction<Project> toCompletion(){ return p -> p == null ? 0.0 : p.calculateCompletionPercentage(); }

    // ---------------------------------------------------------------------
    // Comparators
    // ---------------------------------------------------------------------

    /** Compare tasks by name ASC, nulls last, case-insensitive. */
    public static Comparator<Task> byTaskNameAscNullsLast() {
        return Comparator.comparing(Task::getTaskName,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
    }

    /** Compare tasks by priority: High > Medium > Low > (others/null). Then by name ASC. */
    public static Comparator<Task> byPriorityThenName() {
        return Comparator
                .comparingInt((Task t) -> priorityRank(t == null ? null : t.getPriority()))
                .reversed() // High (3) first
                .thenComparing(byTaskNameAscNullsLast());
    }

    /** Compare tasks by due date ASC, nulls last. */
    public static Comparator<Task> byDueDateAscNullsLast() {
        return (a, b) -> {
            Optional<LocalDate> da = parseIsoDate(a == null ? null : a.getDueDate());
            Optional<LocalDate> db = parseIsoDate(b == null ? null : b.getDueDate());
            if (da.isEmpty() && db.isEmpty()) return 0;
            if (da.isEmpty()) return 1; // nulls last
            if (db.isEmpty()) return -1;
            return da.get().compareTo(db.get());
        };
    }

    /** Compare projects by completion DESC, then by name ASC. */
    public static Comparator<Project> byCompletionDescThenNameAsc() {
        return Comparator
                .comparingDouble(Project::calculateCompletionPercentage)
                .reversed()
                .thenComparing(p -> Optional.ofNullable(p.getProjectName()).orElse(""),
                        String.CASE_INSENSITIVE_ORDER);
    }

    /** Compare projects by ID ASC, nulls last (ID is immutable). */
    public static Comparator<Project> byProjectIdAscNullsLast() {
        return Comparator.comparing(Project::getProjectId,
                Comparator.nullsLast(String::compareTo));
    }

    // ---------------------------------------------------------------------
    // Collectors / Aggregations
    // ---------------------------------------------------------------------

    /** Count tasks by status; null status grouped as "Unknown". */
    public static Collector<Task, ?, Map<String, Long>> countByStatus() {
        return Collectors.groupingBy(
                t -> Optional.ofNullable(t.getStatus()).orElse("Unknown"),
                Collectors.counting()
        );
    }

    /** Group tasks by priority; null/blank priority grouped as "Unknown". */
    public static Collector<Task, ?, Map<String, List<Task>>> groupByPriorityWithUnknown() {
        return Collectors.groupingBy(
                t -> {
                    String p = (t == null ? null : t.getPriority());
                    return (p == null || p.isBlank()) ? "Unknown" : p;
                }
        );
    }

    /** Distinct, non-blank assignees collected into a sorted set (case-insensitive). */
    public static Collector<Task, ?, SortedSet<String>> distinctAssigneesSorted() {
        Comparator<String> ci = String.CASE_INSENSITIVE_ORDER;
        return Collectors.mapping(
                t -> t == null ? null : t.getAssignedTo(),
                Collectors.filtering(
                        s -> s != null && !s.isBlank(),
                        Collectors.toCollection(() -> new TreeSet<>(ci))
                )
        );
    }

    /** Average completion percentage across projects (empty -> 0.0). */
    public static double averageProjectCompletion(Collection<Project> projects) {
        if (projects == null || projects.isEmpty()) return 0.0;
        return projects.stream()
                .mapToDouble(Project::calculateCompletionPercentage)
                .average().orElse(0.0);
    }

    // ---------------------------------------------------------------------
    // Combinators / Utilities
    // ---------------------------------------------------------------------

    /** Negate a predicate (null-safe). */
    public static <T> Predicate<T> not(Predicate<T> p) {
        Objects.requireNonNull(p, "predicate");
        return p.negate();
    }

    /** Conjunction (AND) of predicates. */
    @SafeVarargs
    public static <T> Predicate<T> all(Predicate<T>... preds) {
        Objects.requireNonNull(preds, "preds");
        return Arrays.stream(preds).reduce(t -> true, Predicate::and);
    }

    /** Disjunction (OR) of predicates. */
    @SafeVarargs
    public static <T> Predicate<T> any(Predicate<T>... preds) {
        Objects.requireNonNull(preds, "preds");
        return Arrays.stream(preds).reduce(t -> false, Predicate::or);
    }

    /** Null-safe mapper with default value if input or mapping result is null. */
    public static <T, R> Function<T, R> nullSafe(Function<T, R> fn, R defaultVal) {
        Objects.requireNonNull(fn, "fn");
        return t -> {
            if (t == null) return defaultVal;
            R r = fn.apply(t);
            return r == null ? defaultVal : r;
        };
    }

    // ---------------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------------

    /** Priority ranking: High=3, Medium=2, Low=1, others/null=0. */
    private static int priorityRank(String p) {
        if (p == null) return 0;
        switch (p.trim().toLowerCase(Locale.ROOT)) {
            case "high":   return 3;
            case "medium": return 2;
            case "low":    return 1;
            default:       return 0;
        }
    }

    /** Parse ISO yyyy-MM-dd to LocalDate (null/invalid -> empty). */
    private static Optional<LocalDate> parseIsoDate(String s) {
        if (s == null || s.isBlank()) return Optional.empty();
        try {
            return Optional.of(LocalDate.parse(s));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }
}
