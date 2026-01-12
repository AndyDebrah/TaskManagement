
package main.java.com.example.utils;

import main.java.com.example.models.Project;
import main.java.com.example.models.Task;
import main.java.com.example.services.ProjectService;

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
 */
public final class FileUtils {

    private FileUtils() {}

    /** Container for load results to seed services. */
    public static final class LoadResult {
        public final Project[] projects;
        public final Task[] tasks;
        public LoadResult(Project[] projects, Task[] tasks) {
            this.projects = projects;
            this.tasks = tasks;
        }
    }

    /**
     * Save all projects (and their tasks) to a JSON-like file.
     * Uses try-with-resources and NIO.
     */
    public static void save(ProjectService projectService, Path file) throws IOException {
        Objects.requireNonNull(projectService, "projectService");
        Objects.requireNonNull(file, "file");

        Project[] projects = projectService.getAllProjects();

        // Build JSON-like content
        String content = buildJsonLike(projects);

        // Ensure parent directory exists
        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // try-with-resources for clean resource management
        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            writer.write(content);
        }
    }

    /**
     * Load projects & tasks from JSON-like file. Gracefully handles missing file.
     * Returns empty arrays if file does not exist or is malformed.
     */
    public static LoadResult load(Path file) {
        if (file == null || !Files.exists(file)) {
            return new LoadResult(new Project[0], new Task[0]);
        }

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String raw = reader.lines().collect(Collectors.joining("\n"));
            return parseJsonLike(raw);
        } catch (IOException e) {
            // Malformed or unreadable file: return empty
            return new LoadResult(new Project[0], new Task[0]);
        }
    }

    // ---------- Serialization (JSON-like) ----------

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
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
            for (int j = 0; j < tasks.length; j++) {
                Task t = tasks[j];
                sb.append("        {\n");
                sb.append("          \"id\": \"").append(escape(t.getTaskId())).append("\",\n");
                sb.append("          \"name\": \"").append(escape(t.getTaskName())).append("\",\n");
                sb.append("          \"projectId\": \"").append(escape(t.getProjectId())).append("\",\n");
                sb.append("          \"description\": \"").append(escape(t.getDescription())).append("\",\n");
                sb.append("          \"assignedTo\": \"").append(escape(t.getAssignedTo())).append("\",\n");
                sb.append("          \"priority\": \"").append(escape(t.getPriority())).append("\",\n");
                sb.append("          \"status\": \"").append(escape(t.getStatus())).append("\",\n");
                sb.append("          \"dueDate\": \"").append(escape(t.getDueDate())).append("\"\n");
                sb.append("        }");
                if (j < tasks.length - 1) sb.append(",");
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

    // ---------- Deserialization (JSON-like parsing via regex) ----------

    // Simple regex patterns for key-value pairs
    private static final Pattern STRING_FIELD = Pattern.compile("\"([a-zA-Z]+)\"\\s*:\\s*\"(.*?)\"");
    private static final Pattern NUMBER_FIELD = Pattern.compile("\"([a-zA-Z]+)\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

    private static LoadResult parseJsonLike(String raw) {
        // Split into project blocks: naive split on "},\n    {" between projects
        String projectsSection = extractArray(raw, "projects");
        if (projectsSection == null || projectsSection.isEmpty()) {
            return new LoadResult(new Project[0], new Task[0]);
        }

        List<String> projectBlocks = splitObjects(projectsSection);

        List<Project> loadedProjects = new ArrayList<>();
        List<Task> loadedTasks = new ArrayList<>();

        for (String projectBlock : projectBlocks) {
            Map<String, String> fields = extractFields(projectBlock);

            String id        = fields.getOrDefault("id", "");
            String name      = fields.getOrDefault("name", "");
            String type      = fields.getOrDefault("type", "Generic");
            String desc      = fields.getOrDefault("description", "");
            String startDate = fields.getOrDefault("startDate", "");
            String endDate   = fields.getOrDefault("endDate", "");
            String status    = fields.getOrDefault("status", "Active");
            double budget    = parseDouble(fields.get("budget"), 0.0);
            int teamSize     = parseInt(fields.get("teamSize"), 0);

            // Create a concrete project instance with the ID we read
            Project project = ProjectFactory.create(id, name, desc, startDate, endDate, budget, teamSize, status, type);
            if (project == null) {
                // If factory failed, skip this project
                continue;
            }
            loadedProjects.add(project);

            // Extract tasks array from project block
            String tasksSection = extractArray(projectBlock, "tasks");
            if (tasksSection != null) {
                List<String> taskBlocks = splitObjects(tasksSection);
                for (String taskBlock : taskBlocks) {
                    Map<String, String> tf = extractFields(taskBlock);
                    Task task = new Task(
                            tf.getOrDefault("id", ""),id,
                            tf.getOrDefault("name", ""),
                            tf.getOrDefault("description", ""),
                            tf.getOrDefault("assignedTo", ""),
                            tf.getOrDefault("priority", "Low"),
                            tf.getOrDefault("dueDate", "")
                    );
                    task.setStatus(tf.getOrDefault("status", "Pending"));
                    loadedTasks.add(task);
                    project.addTask(task); // keep the project-task relationship
                }
            }
        }

        return new LoadResult(
                loadedProjects.toArray(new Project[0]),
                loadedTasks.toArray(new Task[0])
        );
    }

    // Extract "[ ... ]" for a named array field: "projects": [ ... ]
    private static String extractArray(String raw, String arrayName) {
        int idx = raw.indexOf("\"" + arrayName + "\"");
        if (idx < 0) return null;
        int start = raw.indexOf('[', idx);
        if (start < 0) return null;

        int depth = 0;
        for (int i = start; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return raw.substring(start + 1, i);
                }
            }
        }
        return null;
    }

    // Split top-level objects within an array: { ... }, { ... }, ...
    private static List<String> splitObjects(String arrayContent) {
        List<String> blocks = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    blocks.add(arrayContent.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return blocks;
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

    private static double parseDouble(String s, double def) {
        try {
            return s == null ? def : Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int parseInt(String s, int def) {
        try {
            return s == null ? def : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    // ---------- Simple factory: choose a concrete Project type ----------

    /**
     * Factory to construct concrete Projects when loading.
     * You can replace this with SoftwareProject / HardwareProject constraints.
     */
    private static final class ProjectFactory {
        static Project create(String id, String name, String desc,
                              String startDate, String endDate,
                              double budget, int teamSize,
                              String status, String type) {
            // If you have SoftwareProject/HardwareProject available, branch here:
            // if ("Software".equalsIgnoreCase(type)) return new SoftwareProject(... with id ...);
            // if ("Hardware".equalsIgnoreCase(type)) return new HardwareProject(... with id ...);
            // Fallback: SimpleProject (generic concrete type)
            return new SimpleProject(id, name, desc, startDate, endDate, budget, teamSize, status, type);
        }
    }

    /**
     * Generic concrete Project used for loading from file when real subclasses are not available.
     * Computes completion by proportion of completed tasks.
     */
    private static final class SimpleProject extends Project {
        private final String type;

        // NOTE: requires a protected constructor in Project that accepts explicit id & status.
        public SimpleProject(String projectId, String projectName, String description,
                             String startDate, String endDate, double budget, int teamSize,
                             String status, String type) {
            super (projectId, projectName, description, startDate, endDate, budget, teamSize, status);
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
        public String getProjectType() {
            return type;
        }

        @Override
        public String getProjectDetails() {
            return "Type: " + type;
        }
    }
}

