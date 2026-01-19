
package com.example.utils;

import com.example.models.Project;
import com.example.models.Task;
import com.example.services.ProjectService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * File persistence utilities (Phase 3).
 * Saves and loads projects & tasks in a simple JSON-like text file.
 *
 * Key guarantees:
 * - Tasks are NEVER treated as projects during load.
 * - Task.projectId is read from "projectId" (exact case); if missing, we fall back to the parent project's id.
 * - Uses the 7-arg Task constructor to preserve JSON-provided task IDs (e.g., TSK0007).
 * - SimpleProject computes completion based on task completion ratio for loaded data.
 */
public final class FileUtils {

    private FileUtils() {}

    /** Wrapper representing loaded data from persistence file. */
    public static final class LoadResult {
        public final Project[] projects;
        public final Task[] tasks;

        public LoadResult(Project[] projects, Task[] tasks) {
            this.projects = projects;
            this.tasks = tasks;
        }
    }

    /* =======================================================================
       SAVE (two overloads)
       ======================================================================= */

    /** Save using a ProjectService source. */
    public static void save(ProjectService projectService, Path file) throws IOException {
        Objects.requireNonNull(projectService, "projectService");
        Objects.requireNonNull(file, "file");
        save(projectService.getAllProjects(), file);
    }

    /** Save using a Project[] source (same writer). */
    public static void save(Project[] projects, Path file) throws IOException {
        Objects.requireNonNull(projects, "projects");
        Objects.requireNonNull(file, "file");

        String content = buildJsonLike(projects);

        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            writer.write(content);
        }
    }

    /* =======================================================================
       LOAD
       ======================================================================= */

    /**
     * Load projects & tasks from a JSON-like file.
     * Returns empty arrays on missing file or parse failure.
     */
    public static LoadResult load(Path file) {
        if (file == null || !Files.exists(file)) {
            return new LoadResult(new Project[0], new Task[0]);
        }

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String raw = reader.lines().collect(Collectors.joining("\n"));
            return parseJsonLike(raw);
        } catch (IOException e) {
            return new LoadResult(new Project[0], new Task[0]);
        }
    }

    /* =======================================================================
       JSON-LIKE WRITER
       ======================================================================= */

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String buildJsonLike(Project[] projects) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"projects\": [\n");

        for (int i = 0; i < projects.length; i++) {
            Project p = projects[i];

            sb.append("    {\n");
            sb.append("      \"id\": \"").append(escape(p.getProjectId())).append("\",\n");
            sb.append("      \"name\": \"").append(escape(p.getProjectName())).append("\",\n");
            sb.append("      \"type\": \"").append(escape(p.getProjectType())).append("\",\n");
            sb.append("      \"description\": \"").append(escape(p.getDescription())).append("\",\n");
            sb.append("      \"startDate\": \"").append(escape(p.getStartDate())).append("\",\n");
            sb.append("      \"endDate\": \"").append(escape(p.getEndDate())).append("\",\n");
            sb.append("      \"status\": \"").append(escape(p.getStatus())).append("\",\n");
            sb.append("      \"budget\": ").append(p.getBudget()).append(",\n");
            sb.append("      \"teamSize\": ").append(p.getTeamSize()).append(",\n");
            sb.append("      \"tasks\": [\n");

            Task[] tasks = p.getTasks();
            for (int t = 0; t < tasks.length; t++) {
                Task task = tasks[t];
                sb.append("        {\n");
                sb.append("          \"id\": \"").append(escape(task.getTaskId())).append("\",\n");
                sb.append("          \"name\": \"").append(escape(task.getTaskName())).append("\",\n");
                sb.append("          \"projectId\": \"").append(escape(task.getProjectId())).append("\",\n");
                sb.append("          \"description\": \"").append(escape(task.getDescription())).append("\",\n");
                sb.append("          \"assignedTo\": \"").append(escape(task.getAssignedTo())).append("\",\n");
                sb.append("          \"priority\": \"").append(escape(task.getPriority())).append("\",\n");
                sb.append("          \"status\": \"").append(escape(task.getStatus())).append("\",\n");
                sb.append("          \"dueDate\": \"").append(escape(task.getDueDate())).append("\"\n");
                sb.append("        }");
                if (t < tasks.length - 1) sb.append(",");
                sb.append("\n");
            }

            sb.append("      ]\n");
            sb.append("    }");
            if (i < projects.length - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("  ]\n}\n");
        return sb.toString();
    }

    /* =======================================================================
       JSON-LIKE PARSER (safe & structure-aware)
       ======================================================================= */

    // Simple key-value patterns
    private static final Pattern STRING_FIELD =
            Pattern.compile("\"([a-zA-Z][a-zA-Z0-9]*)\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern NUMBER_FIELD =
            Pattern.compile("\"([a-zA-Z][a-zA-Z0-9]*)\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    private static FileUtils.LoadResult parseJsonLike(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LoadResult(new Project[0], new Task[0]);
        }

        String projectsSection = extractArray(raw, "projects");
        if (projectsSection == null || projectsSection.isBlank()) {
            return new LoadResult(new Project[0], new Task[0]);
        }

        List<String> projectBlocks = splitTopLevelObjects(projectsSection);

        List<Project> loadedProjects = new ArrayList<>();
        List<Task> loadedTasks = new ArrayList<>();

        for (String projectBlock : projectBlocks) {
            Map<String, String> pf = extractFields(projectBlock);

            String projectId  = pf.getOrDefault("id", "");
            String name       = pf.getOrDefault("name", "");
            String type       = pf.getOrDefault("type", "Generic");
            String desc       = pf.getOrDefault("description", "");
            String startDate  = pf.getOrDefault("startDate", "");
            String endDate    = pf.getOrDefault("endDate", "");
            String status     = pf.getOrDefault("status", "Active");
            double budget     = toDouble(pf.get("budget"), 0.0);
            int teamSize      = toInt(pf.get("teamSize"), 0);

            // Construct a load-time concrete project (task-based completion).
            Project project = ProjectFactory.create(projectId, name, desc, startDate, endDate, budget, teamSize, status, type);
            if (project == null) continue;
            loadedProjects.add(project);

            // Extract & parse this project's tasks
            String tasksSection = extractArray(projectBlock, "tasks");
            if (tasksSection == null || tasksSection.isBlank()) continue;

            List<String> taskBlocks = splitTopLevelObjects(tasksSection);
            for (String taskBlock : taskBlocks) {
                Map<String, String> tf = extractFields(taskBlock);

                String jsonTaskId  = tf.getOrDefault("id", "");
                String jsonProjId  = tf.getOrDefault("projectId", ""); // exact key & case
                String resolvedPid = jsonProjId.isBlank() ? projectId : jsonProjId;

                // Skip malformed tasks
                if (resolvedPid.isBlank()) continue;

                String taskName    = tf.getOrDefault("name", "");
                String description = tf.getOrDefault("description", "");
                String assignedTo  = tf.getOrDefault("assignedTo", "");
                String priorityRaw = tf.getOrDefault("priority", "Low");
                String priority    = toTitleCase(priorityRaw.isBlank() ? "Low" : priorityRaw);
                String dueDate     = tf.getOrDefault("dueDate", "");
                String tStatus     = tf.getOrDefault("status", "Pending");

                // Use 7-arg constructor to preserve taskId from JSON
                Task task = new Task(
                        jsonTaskId,
                        resolvedPid,
                        taskName,
                        description,
                        assignedTo,
                        priority,
                        dueDate
                );
                task.setStatus(tStatus);

                project.addTask(task);
                loadedTasks.add(task);
            }
        }

        return new LoadResult(
                loadedProjects.toArray(new Project[0]),
                loadedTasks.toArray(new Task[0])
        );
    }

    /* =======================================================================
       STRUCTURE-AWARE HELPERS
       ======================================================================= */

    // Extract the "[ ... ]" for a named array field:  "projects": [ ... ]
    private static String extractArray(String raw, String arrayName) {
        int idx = raw.indexOf("\"" + arrayName + "\"");
        if (idx < 0) return null;

        int start = raw.indexOf('[', idx);
        if (start < 0) return null;

        int depth = 0;
        boolean inQuotes = false;

        for (int i = start; i < raw.length(); i++) {
            char c = raw.charAt(i);

            // flip quote state if not escaped
            if (c == '"' && (i == 0 || raw.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) return raw.substring(start + 1, i);
                }
            }
        }
        return null;
    }

    // Split ONLY top-level objects within an array: { ... }, { ... }, ...
    private static List<String> splitTopLevelObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        if (arrayContent == null || arrayContent.isBlank()) return objects;

        int depth = 0;
        int start = -1;
        boolean inQuotes = false;

        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);

            if (c == '"' && (i == 0 || arrayContent.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '{') {
                    if (depth == 0) start = i;
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && start >= 0) {
                        objects.add(arrayContent.substring(start, i + 1));
                        start = -1;
                    }
                }
            }
        }
        return objects;
    }

    // Extract all string/number fields in a block
    private static Map<String, String> extractFields(String block) {
        Map<String, String> map = new HashMap<>();

        Matcher sm = STRING_FIELD.matcher(block);
        while (sm.find()) {
            map.put(sm.group(1), sm.group(2));
        }

        Matcher nm = NUMBER_FIELD.matcher(block);
        while (nm.find()) {
            map.put(nm.group(1), nm.group(2));
        }

        return map;
    }

    private static double toDouble(String s, double def) {
        try { return (s == null) ? def : Double.parseDouble(s); }
        catch (NumberFormatException e) { return def; }
    }

    private static int toInt(String s, int def) {
        try { return (s == null) ? def : Integer.parseInt(s); }
        catch (NumberFormatException e) { return def; }
    }

    private static String toTitleCase(String s) {
        if (s == null || s.isBlank()) return s;
        String lower = s.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    /* =======================================================================
       SIMPLE GENERIC PROJECT (Used only during load)
       ======================================================================= */

    /**
     * Factory to construct concrete Projects when loading.
     * If your domain needs SoftwareProject/HardwareProject during load, route here.
     */
    private static final class ProjectFactory {
        static Project create(String id, String name, String desc,
                              String startDate, String endDate,
                              double budget, int teamSize,
                              String status, String type) {
            // If you want: branch to SoftwareProject/HardwareProject based on "type".
            // Currently we use a simple generic implementation with task-based completion.
            return new SimpleProject(id, name, desc, startDate, endDate, budget, teamSize, status, type);
        }
    }

    /**
     * Generic concrete Project used only for loading.
     * Computes completion by proportion of completed tasks.
     */
    private static final class SimpleProject extends Project {
        private final String type;

        public SimpleProject(String projectId, String projectName, String description,
                             String startDate, String endDate, double budget, int teamSize,
                             String status, String type) {
            super(projectId, projectName, description, startDate, endDate, budget, teamSize, status);
            this.type = (type == null || type.isBlank()) ? "Generic" : type;
        }

        @Override
        public double calculateCompletionPercentage() {
            Task[] tasks = getTasks();
            if (tasks.length == 0) return 0.0;
            long completed = Arrays.stream(tasks).filter(Task::isCompleted).count();
            return (completed * 100.0) / tasks.length;
        }

        @Override
        public String getProjectType() { return type; }

        @Override
        public String getProjectDetails() { return "Type: " + type; }
    }
}
