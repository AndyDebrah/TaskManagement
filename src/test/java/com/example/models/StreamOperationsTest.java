
package com.example.models;

import com.example.models.Project;
import com.example.models.Task;
import com.example.services.StreamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Stream-based operations over Projects and Tasks.
 * Validates filter/map/reduce/summary stats workflows (Week 3) and
 * extended operations aligned with the regenerated StreamService.
 */
public class StreamOperationsTest {

    private StreamService streamService;
    private Project projectA; // will have some completed tasks
    private Project projectB; // will have none completed
    private Project projectC; // empty project

    @BeforeEach
    void setup() {
        streamService = new StreamService();

        // Create three test projects using a small concrete subclass of Project
        projectA = new TestProject("PRJ0001", "Alpha", "Desc",
                "2026-01-01", "2026-01-31", 1000, 3, "Active", "Software");
        projectB = new TestProject("PRJ0002", "Beta", "Desc",
                "2026-02-01", "2026-02-28", 2000, 4, "Active", "Hardware");
        projectC = new TestProject("PRJ0003", "Gamma", "Desc",
                "2026-03-01", "2026-03-31", 1200, 2, "Active", "Generic");

        // Add tasks to A (2 completed, 1 not)
        Task a1 = new Task("PRJ0001", "Design", "Design module", "USR1", "High", "2026-01-05");
        a1.setStatus("Completed");
        Task a2 = new Task("PRJ0001", "Build", "Build module", "USR2", "Medium", "2026-01-15");
        a2.setStatus("In Progress");
        Task a3 = new Task("PRJ0001", "Test", "Test module", "USR3", "Low", "2026-01-20");
        a3.setStatus("Completed");

        projectA.addTask(a1);
        projectA.addTask(a2);
        projectA.addTask(a3);

        // Add tasks to B (all not completed)
        Task b1 = new Task("PRJ0002", "Assemble", "Assemble board", "USR4", "High", "2026-02-10");
        b1.setStatus("Pending");
        Task b2 = new Task("PRJ0002", "QA", "Quality checks", "USR5", "Medium", "2026-02-20");
        b2.setStatus("In Progress");

        projectB.addTask(b1);
        projectB.addTask(b2);

        // C intentionally has no tasks
    }

    // ------------------------------------------------------------------------------------
    // Original tests (kept and slightly generalized to include projectC where useful)
    // ------------------------------------------------------------------------------------

    @Test
    void listCompletedTasks_returnsOnlyCompleted() {
        var completedA = streamService.listCompletedTasks(projectA);
        assertEquals(2, completedA.size(), "Project A should have 2 completed tasks");
        assertTrue(completedA.stream().allMatch(Task::isCompleted));

        var completedB = streamService.listCompletedTasks(projectB);
        assertEquals(0, completedB.size(), "Project B should have 0 completed tasks");

        var completedC = streamService.listCompletedTasks(projectC);
        assertTrue(completedC.isEmpty(), "Project C has no tasks");
    }

    @Test
    void mapTaskNames_returnsProjectedNames() {
        var namesA = streamService.mapTaskNames(projectA);
        assertTrue(namesA.containsAll(List.of("Design", "Build", "Test")));
        assertEquals(3, namesA.size());
    }

    @Test
    void reduceTotalCompletion_sums100ForCompletedElse0() {
        int sumA = streamService.reduceTotalCompletion(projectA); // 2 completed → 200
        int sumB = streamService.reduceTotalCompletion(projectB); // 0 completed → 0
        int sumC = streamService.reduceTotalCompletion(projectC); // 0 tasks → 0

        assertEquals(200, sumA);
        assertEquals(0, sumB);
        assertEquals(0, sumC);
    }

    @Test
    void taskCompletionStats_summarizesDistribution() {
        var statsA = streamService.taskCompletionStats(projectA);
        // Values are [100, 0, 100] in this simplified model
        assertEquals(3, statsA.getCount());
        assertEquals(0, statsA.getMin());
        assertEquals(100, statsA.getMax());
        assertEquals(200 / 3.0, statsA.getAverage(), 0.0001);
        assertEquals(200, statsA.getSum());
    }

    @Test
    void listCompletedProjects_filtersByProjectIsCompleted() {
        // Mark projectA "Completed" for test purposes
        projectA.setStatus("Completed");

        var list = streamService.listCompletedProjects(List.of(projectA, projectB));
        assertEquals(1, list.size());
        assertEquals("PRJ0001", list.get(0).getProjectId());
    }

    @Test
    void mapProjectNames_projectsToNames() {
        var names = streamService.mapProjectNames(List.of(projectA, projectB));
        assertEquals(List.of("Alpha", "Beta"), names);
    }

    @Test
    void averageProjectCompletion_acrossProjects() {
        // A: 2/3 completed -> ~66.666..., B: 0/2 -> 0
        // average = (66.666... + 0) / 2 ≈ 33.333...
        double avg = streamService.averageProjectCompletion(List.of(projectA, projectB));
        assertEquals(33.333, avg, 0.5); // small tolerance
    }

    // ---------------------------------------------------------
    // Small test-only concrete Project implementation (from your original file)
    // ---------------------------------------------------------
    private static class TestProject extends Project {
        private final String type;

        public TestProject(String projectId, String projectName, String description,
                           String startDate, String endDate, double budget, int teamSize,
                           String status, String type) {
            super(projectId, projectName, description, startDate, endDate, budget, teamSize, status);
            this.type = (type == null || type.isBlank()) ? "Generic" : type;
        }

        @Override
        public double calculateCompletionPercentage() {
            Task[] tasks = getTasks();
            if (tasks.length == 0) return 0.0;
            long completed = java.util.Arrays.stream(tasks).filter(Task::isCompleted).count();
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

    // ------------------------------------------------------------------------------------
    // New tests for extended StreamService functionality
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("distinctAssignees: returns sorted, distinct, case-insensitive, excludes blanks")
    void distinctAssignees_sortedAndDistinct() {
        // Baseline
        var assigneesA = streamService.distinctAssignees(projectA);
        assertEquals(List.of("USR1", "USR2", "USR3"), assigneesA);

        // Add duplicate in different case + blank to verify behavior
        Task dup = new Task("PRJ0001", "Docs", "Write docs", "usr1", "Low", "2026-01-25");
        dup.setStatus("Pending");
        projectA.addTask(dup);
        Task blank = new Task("PRJ0001", "Misc", "Misc work", "   ", "Low", "2026-01-25");
        blank.setStatus("Pending");
        projectA.addTask(blank);

        var after = streamService.distinctAssignees(projectA);
        assertEquals(List.of("USR1", "USR2", "USR3"), after);
    }

    @Test
    @DisplayName("countTasksByStatus: counts per status and groups null as 'Unknown'")
    void countTasksByStatus_groupsUnknown() {
        Task unknown = new Task("PRJ0001", "Integrate", "Integration", "USR9", "High", "2026-01-22");
        unknown.setStatus(null);
        projectA.addTask(unknown);

        Map<String, Long> counts = streamService.countTasksByStatus(projectA);
        assertEquals(2L, counts.getOrDefault("Completed", 0L));
        assertEquals(1L, counts.getOrDefault("In Progress", 0L));
        assertEquals(1L, counts.getOrDefault("Unknown", 0L));
    }

    @Test
    @DisplayName("groupTasksByPriority: groups tasks, using 'Unknown' for null/blank priority")
    void groupTasksByPriority_groupsUnknown() {
        Task blankPriority = new Task("PRJ0001", "Plan", "Planning", "USR7", "   ", "2026-01-02");
        projectA.addTask(blankPriority);

        var grouped = streamService.groupTasksByPriority(projectA);

        assertTrue(grouped.containsKey("High"));
        assertTrue(grouped.containsKey("Medium"));
        assertTrue(grouped.containsKey("Low"));
        assertTrue(grouped.containsKey("Unknown"));

        assertEquals(1, grouped.get("High").size());    // Design
        assertEquals(1, grouped.get("Medium").size());  // Build
        assertEquals(1, grouped.get("Low").size());     // Test
        assertEquals(1, grouped.get("Unknown").size()); // Plan
    }

    @Test
    @DisplayName("topNTasksByName: returns tasks sorted by name ASC (nulls last), limited to N")
    void topNTasksByName_sortedAscLimited() {
        var top2 = streamService.topNTasksByName(projectA, 2)
                .stream().map(Task::getTaskName).collect(Collectors.toList());
        assertEquals(List.of("Build", "Design"), top2);

        var top0 = streamService.topNTasksByName(projectA, 0);
        assertTrue(top0.isEmpty());

        var topExcess = streamService.topNTasksByName(projectA, 10);
        assertEquals(3, topExcess.size());
    }

    @Test
    @DisplayName("listCompletedTasksParallel: equals sequential result")
    void listCompletedTasksParallel_sameAsSequential() {
        var seq = streamService.listCompletedTasks(projectA);
        var par = streamService.listCompletedTasksParallel(projectA);

        assertEquals(seq.size(), par.size());
        assertEquals(new HashSet<>(seq), new HashSet<>(par));
    }

    @Test
    @DisplayName("sortProjectsById: ascending by ID")
    void sortProjectsById_ascending() {
        var shuffled = new ArrayList<>(List.of(projectB, projectC, projectA));
        var sorted = streamService.sortProjectsById(shuffled);
        assertEquals(List.of("PRJ0001", "PRJ0002", "PRJ0003"),
                sorted.stream().map(Project::getProjectId).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("sortProjectsByCompletionDesc: completion desc, then name asc")
    void sortProjectsByCompletionDesc_descThenName() {
        // A has ~66.67, B has 0, C has 0
        var sorted = streamService.sortProjectsByCompletionDesc(List.of(projectB, projectC, projectA));
        assertEquals("Alpha", sorted.get(0).getProjectName()); // Highest completion first
        // Both B and C are 0% — names asc expected: Beta then Gamma
        assertEquals("Beta", sorted.get(1).getProjectName());
        assertEquals("Gamma", sorted.get(2).getProjectName());
    }

    @Test
    @DisplayName("Array overloads: listCompletedTasks / mapTaskNames / reduceTotalCompletion / taskCompletionStats")
    void arrayOverloads_taskOps() {
        Task[] aTasks = projectA.getTasks();
        Task[] bTasks = projectB.getTasks();

        var completedA = streamService.listCompletedTasks(aTasks);
        assertEquals(2, completedA.size());

        var namesA = streamService.mapTaskNames(aTasks);
        assertEquals(Set.of("Design", "Build", "Test"), new HashSet<>(namesA));

        int sumA = streamService.reduceTotalCompletion(aTasks);
        assertEquals(200, sumA);

        var statsB = streamService.taskCompletionStats(bTasks);
        assertEquals(2, statsB.getCount());
        assertEquals(0, statsB.getSum());
    }

    @Test
    @DisplayName("Array overload: averageProjectCompletion(Project[])")
    void arrayOverload_averageProjectCompletion() {
        Project[] arr = new Project[] { projectA, projectB, projectC };
        double avg = streamService.averageProjectCompletion(arr);
        // A ≈ 66.67, B = 0, C = 0 -> ~22.22
        assertEquals(22.222, avg, 1.0); // allow tolerance
    }
}
