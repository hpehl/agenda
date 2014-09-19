package org.jboss.metrics.agenda;

import static org.jboss.metrics.agenda.TaskDefinition.TimeUnit.SECOND;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TaskDefinitionTest {

    @Test(expected = AssertionError.class)
    public void invalidAddress() {
        new TaskDefinition(null, "foo", 1);
    }

    @Test(expected = AssertionError.class)
    public void invalidAttribute() {
        new TaskDefinition("foo", null, 1);
    }

    @Test(expected = AssertionError.class)
    public void invalidInterval() {
        new TaskDefinition("foo", "bar", 0);
    }

    @Test
    public void defaultInterval() {
        TaskDefinition taskDefinition = new TaskDefinition("foo", "bar", 1);
        assertEquals(SECOND, taskDefinition.getUnit());
    }
}