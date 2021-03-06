package org.jboss.metrics.agenda.impl;

import static org.jboss.metrics.agenda.Interval.EACH_SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Operation;
import org.jboss.metrics.agenda.OperationBuilder;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskGroup;
import org.jboss.metrics.agenda.TestData;
import org.junit.Before;
import org.junit.Test;

public class ReadAttributeOperationBuilderTest {

    private OperationBuilder operationBuilder;

    @Before
    public void setUp() {
        operationBuilder = new ReadAttributeOperationBuilder();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGroup() {
        operationBuilder.createOperation(new TaskGroup(EACH_SECOND));
    }

    @Test
    public void simpleOp() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(TestData.fooTask(EACH_SECOND));
        Set<Operation> operations = operationBuilder.createOperation(group);

        assertEquals(1, operations.size());
        ModelNode modelNode = operations.iterator().next().getModelNode();
        assertFalse(modelNode.get("address").asList().isEmpty());
        assertEquals("read-attribute", modelNode.get("operation").asString());
    }

    @Test
    public void compOp() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(new Task("/a=b", "c", EACH_SECOND));
        group.addTask(new Task("/x=y", "z", EACH_SECOND));
        Set<Operation> operations = operationBuilder.createOperation(group);

        assertEquals(1, operations.size());
        ModelNode modelNode = operations.iterator().next().getModelNode();
        assertTrue(modelNode.get("address").asList().isEmpty());
        assertEquals("composite", modelNode.get("operation").asString());
        assertEquals(2, modelNode.get("steps").asList().size());
    }
}