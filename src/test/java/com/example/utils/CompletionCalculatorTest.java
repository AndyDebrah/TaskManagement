package test.java.com.example.utils;

import main.java.com.example.models.Task;
import main.java.com.example.utils.CompletionCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompletionCalculatorTest {

    private Task t1, t2, t3, t4;

    @BeforeEach
    void setUp() {
        t1 = new Task("PRJ001", "Feature A", "Desc", "USR001", "High", "2026-01-10");
        t2 = new Task("PRJ001", "Feature B", "Desc", "USR002", "Medium", "2026-01-11");
        t3 = new Task("PRJ001", "Feature C", "Desc", "USR003", "Low", "2026-01-12");
        t4 = new Task("PRJ001", "Feature D", "Desc", "USR004", "Low", "2026-01-13");
    }

    @Test
    void whenNoTasks_returnsZero() {
        assertEquals(0.0, CompletionCalculator.calculateCompletion(new Task[0]), 1e-6);
        assertEquals(0.0, CompletionCalculator.calculateCompletion((List<Task>) null), 1e-6);
    }

    @Test
    void whenPartialCompletion_returnsCorrectPercent() {
        t1.completeTask();
        t2.completeTask();
        Task[] tasks = new Task[]{t1, t2, t3, t4};
        assertEquals(50.0, CompletionCalculator.calculateCompletion(tasks), 1e-6);

        List<Task> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        list.add(t3);
        list.add(t4);
        assertEquals(50.0, CompletionCalculator.calculateCompletion(list), 1e-6);
    }

    @Test
    void whenAllCompleted_returnsHundred() {
        t1.completeTask();
        t2.completeTask();
        t3.completeTask();
        Task[] tasks = new Task[]{t1, t2, t3};
        assertEquals(100.0, CompletionCalculator.calculateCompletion(tasks), 1e-6);
    }

    @Test
    void handlesNullElementsGracefully() {
        Task[] tasks = new Task[]{t1, null, t2};
        t1.completeTask();
        assertEquals(50.0, CompletionCalculator.calculateCompletion(tasks), 1e-6);
    }
}
