package com.example.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.models.Project;
import com.example.models.Task;
import com.example.services.ProjectService;
import com.example.services.TaskService;

public class FilePersistenceTest {

    private Path tempFile;

    @BeforeEach
    void setup() throws Exception {
        tempFile = Paths.get("data", "test_projects_data.json");
        if (Files.exists(tempFile)) Files.delete(tempFile);
        Files.createDirectories(tempFile.getParent());
    }

    @AfterEach
    void cleanup() throws Exception {
        if (Files.exists(tempFile)) Files.delete(tempFile);
    }

    @Test
    void saveThenLoadRoundTrip() throws Exception {
        ProjectService ps = new ProjectService(Seed.seedProjects());
        TaskService ts = new TaskService(Seed.seedTasks(), ps);

        // Get actual project IDs and add tasks to their projects
        Project[] allProjects = ps.getAllProjects();
        Task[] allTasks = Seed.seedTasks();
        
        // Map tasks to projects by index (since Seed creates 3 projects and 3 tasks in matching order)
        int taskIndex = 0;
        for (Project p : allProjects) {
            if (p != null && taskIndex < allTasks.length && allTasks[taskIndex] != null) {
                p.addTask(allTasks[taskIndex]);
                taskIndex++;
            }
        }

        // save
        FileUtils.save(ps, tempFile);
        assertTrue(Files.exists(tempFile));

        // load
        FileUtils.LoadResult loaded = FileUtils.load(tempFile);
        assertTrue(loaded.projects.length > 0);
        ProjectService ps2 = new ProjectService(loaded.projects);
        TaskService ts2 = new TaskService(loaded.tasks, ps2);

        assertEquals(ps.getProjectCount(), ps2.getProjectCount());
        assertEquals(ts.getTaskCount(), ts2.getTaskCount());
    }

    @Test
    void loadMissingFileReturnsEmpty() {
        Path missing = Paths.get("data", "does_not_exist.json");
        FileUtils.LoadResult lr = FileUtils.load(missing);
        assertEquals(0, lr.projects.length);
        assertEquals(0, lr.tasks.length);
    }

    @Test
    void loadMalformedFileReturnsEmpty() throws Exception {
        Files.writeString(tempFile, "not a json-like structure");
        FileUtils.LoadResult lr = FileUtils.load(tempFile);
        assertEquals(0, lr.projects.length);
        assertEquals(0, lr.tasks.length);
    }
}
