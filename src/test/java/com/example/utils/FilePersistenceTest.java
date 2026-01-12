package test.java.com.example.utils;

import main.java.com.example.models.Task;
import main.java.com.example.services.ProjectService;
import main.java.com.example.services.TaskService;
import main.java.com.example.utils.FileUtils;
import main.java.com.example.utils.Seed;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

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
