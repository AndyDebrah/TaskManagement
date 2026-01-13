
package main.java.com.example.services;

import main.java.com.example.models.Project;
import main.java.com.example.models.Task;
import main.java.com.example.utils.FunctionalUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream-based utilities for tasks and projects.
 * This version aligns with FunctionalUtils for predicates, comparators, and collectors.
 */
public class StreamService {

    // ------------------------ Snapshot helpers ------------------------

    /** Null-safe snapshot of a project's tasks as a List. */
    private List<Task> snapshotTasks(Project project) {
        if (project == null) return List.of();
        // Project.getTasks() returns a fresh array; wrap it as a fixed-size list
        Task[] arr = project.getTasks();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    /** Null-safe snapshot of projects from a ProjectService source. */
    private List<Project> snapshotProjects(ProjectService projectService) {
        if (projectService == null) return List.of();
        Project[] arr = projectService.getAllProjects();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    // ------------------------ Stream helpers (null-safe) ------------------------

    private Stream<Task> streamTasks(Collection<Task> tasks) {
        return tasks == null ? Stream.empty() : tasks.stream().filter(Objects::nonNull);
    }

    private Stream<Task> streamTasks(Task[] tasks) {
        return tasks == null ? Stream.empty() : Arrays.stream(tasks).filter(Objects::nonNull);
    }

    private Stream<Project> streamProjects(Collection<Project> projects) {
        return projects == null ? Stream.empty() : projects.stream().filter(Objects::nonNull);
    }

    private Stream<Project> streamProjects(Project[] projects) {
        return projects == null ? Stream.empty() : Arrays.stream(projects).filter(Objects::nonNull);
    }

    // ------------------------ Task operations ------------------------

    /** List completed tasks for a project. */
    public List<Task> listCompletedTasks(Project project) {
        return streamTasks(snapshotTasks(project))
                .filter(FunctionalUtils.isCompletedTask())
                .collect(Collectors.toList());
    }

    /** Filter tasks with a caller-provided predicate. */
    public List<Task> filterTasks(Project project, Predicate<Task> predicate) {
        return streamTasks(snapshotTasks(project))
                .filter(Objects.requireNonNull(predicate, "predicate"))
                .collect(Collectors.toList());
    }

    /** Map task names (keeps null names if present; filter if you prefer). */
    public List<String> mapTaskNames(Project project) {
        return streamTasks(snapshotTasks(project))
                .map(Task::getTaskName)
                .collect(Collectors.toList());
    }

    /** Distinct assignees, sorted case-insensitively and excluding blanks. */
    public List<String> distinctAssignees(Project project) {
        return new ArrayList<>(
                streamTasks(snapshotTasks(project))
                        .collect(FunctionalUtils.distinctAssigneesSorted())
        );
    }

    /** Count tasks by status with null grouped as "Unknown". */
    public Map<String, Long> countTasksByStatus(Project project) {
        return streamTasks(snapshotTasks(project)).collect(FunctionalUtils.countByStatus());
    }

    /** Group tasks by priority; null/blank grouped as "Unknown". */
    public Map<String, List<Task>> groupTasksByPriority(Project project) {
        return streamTasks(snapshotTasks(project)).collect(FunctionalUtils.groupByPriorityWithUnknown());
    }

    /** Top-N tasks sorted by name ASC (nulls last). */
    public List<Task> topNTasksByName(Project project, int n) {
        int limit = Math.max(0, n);
        return streamTasks(snapshotTasks(project))
                .sorted(FunctionalUtils.byTaskNameAscNullsLast())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /** Reduce: sum of completion contribution (Completed=100, else 0). */
    public int reduceTotalCompletion(Project project) {
        return streamTasks(snapshotTasks(project))
                .mapToInt(t -> t.isCompleted() ? 100 : 0)
                .sum();
    }

    /** Summary statistics over completion contribution (Completed=100, else 0). */
    public IntSummaryStatistics taskCompletionStats(Project project) {
        return streamTasks(snapshotTasks(project))
                .mapToInt(t -> t.isCompleted() ? 100 : 0)
                .summaryStatistics();
    }

    /** Find first task matching a predicate. */
    public Optional<Task> findFirstTask(Project project, Predicate<Task> predicate) {
        return streamTasks(snapshotTasks(project))
                .filter(Objects.requireNonNull(predicate, "predicate"))
                .findFirst();
    }

    // ------------------------ Project operations (ProjectService source) ------------------------

    /** List completed projects from a ProjectService (Project::isCompleted is 100%). */
    public List<Project> listCompletedProjects(ProjectService projectService) {
        return streamProjects(snapshotProjects(projectService))
                .filter(FunctionalUtils.isCompletedProject())
                .collect(Collectors.toList());
    }

    /** Map project names from a ProjectService source. */
    public List<String> mapProjectNames(ProjectService projectService) {
        return streamProjects(snapshotProjects(projectService))
                .map(Project::getProjectName)
                .collect(Collectors.toList());
    }

    /** Average project completion from ProjectService source (empty -> 0.0). */
    public double averageProjectCompletion(ProjectService projectService) {
        return streamProjects(snapshotProjects(projectService))
                .mapToDouble(Project::calculateCompletionPercentage)
                .average().orElse(0.0);
    }

    /** Parallel average project completion over ProjectService source. */
    public double averageProjectCompletionParallel(ProjectService projectService) {
        return snapshotProjects(projectService).parallelStream()
                .mapToDouble(Project::calculateCompletionPercentage)
                .average().orElse(0.0);
    }

    // ------------------------ Project operations (Collections / arrays) ------------------------

    /** List completed projects from a collection. */
    public List<Project> listCompletedProjects(List<Project> projects) {
        return streamProjects(projects)
                .filter(FunctionalUtils.isCompletedProject())
                .collect(Collectors.toList());
    }

    /** Map project names from a collection. */
    public List<String> mapProjectNames(List<Project> projects) {
        return streamProjects(projects)
                .map(Project::getProjectName)
                .collect(Collectors.toList());
    }

    /** Average project completion across a collection (empty -> 0.0). */
    public double averageProjectCompletion(List<Project> projects) {
        return streamProjects(projects)
                .mapToDouble(Project::calculateCompletionPercentage)
                .average().orElse(0.0);
    }

    /** Sort projects by ID (ascending, nulls last). */
    public List<Project> sortProjectsById(List<Project> projects) {
        return streamProjects(projects)
                .sorted(FunctionalUtils.byProjectIdAscNullsLast())
                .collect(Collectors.toList());
    }

    /** Sort projects by completion descending, then name ascending. */
    public List<Project> sortProjectsByCompletionDesc(List<Project> projects) {
        return streamProjects(projects)
                .sorted(FunctionalUtils.byCompletionDescThenNameAsc())
                .collect(Collectors.toList());
    }

    // ------------------------ Parallel tasks ------------------------

    /** Parallel listing of completed tasks (using snapshot + parallel). */
    public List<Task> listCompletedTasksParallel(Project project) {
        return snapshotTasks(project).parallelStream()
                .filter(FunctionalUtils.isCompletedTask())
                .collect(Collectors.toList());
    }

    // ------------------------ Convenience overloads for arrays ------------------------

    public List<Task> listCompletedTasks(Task[] tasks) {
        return streamTasks(tasks)
                .filter(FunctionalUtils.isCompletedTask())
                .collect(Collectors.toList());
    }

    public List<String> mapTaskNames(Task[] tasks) {
        return streamTasks(tasks)
                .map(Task::getTaskName)
                .collect(Collectors.toList());
    }

    public int reduceTotalCompletion(Task[] tasks) {
        return streamTasks(tasks)
                .mapToInt(t -> t.isCompleted() ? 100 : 0)
                .sum();
    }

    public IntSummaryStatistics taskCompletionStats(Task[] tasks) {
        return streamTasks(tasks)
                .mapToInt(t -> t.isCompleted() ? 100 : 0)
                .summaryStatistics();
    }

    public double averageProjectCompletion(Project[] projects) {
        return streamProjects(projects)
                .mapToDouble(Project::calculateCompletionPercentage)
                .average().orElse(0.0);
    }
}
