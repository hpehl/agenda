package org.jboss.metrics.agenda;

import static org.jboss.metrics.agenda.TaskDefinition.TimeUnit.SECOND;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TaskDefinitionTest {

    @Test
    public void defaultInterval() {
        TaskDefinition taskDefinition = new TaskDefinition("foo", "bar", 1);
        assertEquals(SECOND, taskDefinition.getUnit());
    }
}