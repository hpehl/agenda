package org.jboss.metrics.agenda;

import static org.junit.Assert.assertNotEquals;

import org.jboss.dmr.ModelNode;
import org.junit.Test;

public class OperationTest {

    @Test
    public void uniqueId() {
        ModelNode operation = new ModelNode();
        Operation operation1 = new Operation(operation);
        Operation operation2 = new Operation(operation);

        assertNotEquals(operation1, operation2);
        assertNotEquals(operation1.getId(), operation2.getId());
    }

    @Test
    public void composite() {
        ModelNode operation = new ModelNode();
        Operation step1 = new Operation(operation);
        Operation step2 = new Operation(operation);
        Operation step3 = new Operation(operation);
//        Operation task = new Operation(Arrays.asList(step1, step2, step3));
//        ModelNode comp = task.getOperation();

//        assertTrue(comp.get("address").asList().isEmpty());
//        assertEquals("composite", comp.get("operation").asString());
//        assertEquals(3, comp.get("steps").asList().size());
    }
}