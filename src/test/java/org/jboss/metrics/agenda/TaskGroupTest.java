package org.jboss.metrics.agenda;

import static org.jboss.metrics.agenda.Interval.EACH_MINUTE;
import static org.jboss.metrics.agenda.Interval.EACH_SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TaskGroupTest {

    @Test
    public void defaults() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        assertEquals(TaskGroup.ANY_HOST, group.getHost());
        assertEquals(TaskGroup.ANY_SERVER, group.getServer());
    }

    @Test
    public void equals() {
        TaskGroup g1 = new TaskGroup(EACH_SECOND);
        TaskGroup g2 = new TaskGroup(EACH_SECOND);
        assertEquals(g1, g2);
        assertNotEquals(g1.getId(), g2.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(TestData.fooTask(EACH_SECOND));
        group.iterator().remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void interval() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(TestData.fooTask(EACH_MINUTE));
    }
}