package com.example.models;

import com.example.models.SoftwareProject;
import com.example.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskTest {
    private Task t;

    @BeforeEach
    void setUp() {
        t = new Task("PRJ1", "Task A", "Desc", "User1", "High", "2026-01-10");
    }

    @Test
    void newTask_isPending_and_notCompleted() {
        assertEquals("Pending", t.getStatus());
        assertFalse(t.isCompleted());
        assertEquals(0.0, t.getCompletionPercentage(), 1e-6);
    }

    @Test
    void completeTask_changesStatus_and_completion() {
        boolean changed = t.markAsCompleted();
        assertTrue(changed);
        assertEquals("Completed", t.getStatus());
        assertTrue(t.isCompleted());
        assertEquals(100.0, t.getCompletionPercentage(), 1e-6);

        // calling again returns false (already completed)
        assertFalse(t.markAsCompleted());
    }

    @Test
    void add_and_remove_task_from_project() {
        SoftwareProject p = new SoftwareProject("S", "D", "2026-01-01", "2026-12-31", 1000.0, 2, "Java", "Agile", 1);
        p.addTask(t);
        Task[] tasks = p.getTasks();
        assertEquals(1, tasks.length);
        assertEquals(t.getTaskId(), tasks[0].getTaskId());
        p.removeTask(t.getTaskId());
        assertEquals(0, p.getTasks().length);
    }
}
