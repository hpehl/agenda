package org.jboss.metrics.agenda;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TaskDefinitionTest {

    @Test(expected = IllegalArgumentException.class)
    public void illegalUnitNano() {
        new TaskDefinition("foo", "bar", 1, NANOSECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalUnitMicro() {
        new TaskDefinition("foo", "bar", 1, MICROSECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalUnitMilli() {
        new TaskDefinition("foo", "bar", 1, MILLISECONDS);
    }

    @Test
    public void defaultInterval() {
        TaskDefinition taskDefinition = new TaskDefinition("foo", "bar", 1);
        assertEquals(SECONDS, taskDefinition.getUnit());
    }
}