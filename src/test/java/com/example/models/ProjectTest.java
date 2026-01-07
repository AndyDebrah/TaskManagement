package test.java.com.example.models;

import main.java.com.example.models.HardwareProject;
import main.java.com.example.models.SoftwareProject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectTest {

    @Test
    public void softwareProject_noFeatures_returnsZero() {
        SoftwareProject p = new SoftwareProject("Soft", "Desc", "2026-01-01", "2026-12-31", 10000.0, 5,
                "Java", "Agile", 0);
        assertEquals(0.0, p.calculateCompletionPercentage(), 1e-6);
    }

    @Test
  public   void softwareProject_partialAndComplete() {
        SoftwareProject p = new SoftwareProject("Soft", "Desc", "2026-01-01", "2026-12-31", 10000.0, 5,
                "Java", "Agile", 4);
        p.setCompletedFeatures(2);
        assertEquals(50.0, p.calculateCompletionPercentage(), 1e-6);
        p.setCompletedFeatures(4);
        assertEquals(100.0, p.calculateCompletionPercentage(), 1e-6);
    }

    @Test
   public void hardwareProject_componentsAndPrototype() {
        HardwareProject h = new HardwareProject("Hard", "Desc", "2026-01-01", "2026-12-31", 20000.0, 3,
                "PCB", 5);
        assertEquals(0.0, h.calculateCompletionPercentage(), 1e-6);
        h.setAssembledComponents(2);
        // component progress = (2*80)/5 = 32.0
        assertEquals(32.0, h.calculateCompletionPercentage(), 1e-6);
        h.setPrototypeCompleted(true);
        // now +20 => 52.0
        assertEquals(52.0, h.calculateCompletionPercentage(), 1e-6);
        h.setAssembledComponents(5);
        // assembled==total and prototypeCompleted => 100
        assertEquals(100.0, h.calculateCompletionPercentage(), 1e-6);
    }
}
