package org.jboss.metrics.agenda.impl;

import org.jboss.metrics.agenda.OperationBuilder;
import org.junit.Before;
import org.junit.Test;

public class ReadAttributeOperationBuilderTest {

    private OperationBuilder operationBuilder;

    @Before
    public void setUp() {
        operationBuilder = new ReadAttributeOperationBuilder();
    }

    @Test
    public void createTask() {
//        Task td = new Task("/subsystem=datasources/data-source=ExampleDS/statistics=pool",
//                "ActiveCount", 5, SECONDS);

//        Operation task = operationBuilder.createOperation(td);
//        assertNotNull(task);
//
//        ModelNode operation = task.getOperation();
//        assertNotNull(operation);
//
//        ModelNode expected = new ModelNode();
//        expected.get("address").add("subsystem", "datasources");
//        expected.get("address").add("data-source", "ExampleDS");
//        expected.get("address").add("statistics", "pool");
//        expected.get("operation").set("read-attribute");
//        expected.get("name").set("ActiveCount");
//        assertEquals(expected, operation);
    }
}