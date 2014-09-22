package org.jboss.metrics.agenda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

public class TaskTest {

    @Test
    public void uniqueId() {
        ModelNode operation = new ModelNode();
        Task task1 = new Task(operation);
        Task task2 = new Task(operation);

        assertNotEquals(task1, task2);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    public void composite() {
        ModelNode operation = new ModelNode();
        Task step1 = new Task(operation);
        Task step2 = new Task(operation);
        Task step3 = new Task(operation);
        Task task = new Task(Arrays.asList(step1, step2, step3));
        ModelNode comp = task.getOperation();

        assertTrue(comp.get("address").asList().isEmpty());
        assertEquals("composite", comp.get("operation").asString());
        assertEquals(3, comp.get("steps").asList().size());
    }
}