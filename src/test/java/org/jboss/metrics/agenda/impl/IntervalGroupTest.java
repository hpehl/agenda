package org.jboss.metrics.agenda.impl;

import com.google.common.collect.Iterators;
import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntervalGroupTest {

    private IntervalGroup taskGroup;

    @Before
    public void setUp() {
        this.taskGroup = new IntervalGroup(1000);
    }

    @Test
    public void initialTasks() {
        taskGroup.addTask(new Task(new ModelNode()));
        taskGroup.addTask(new Task(new ModelNode()));
        taskGroup.addTask(new Task(new ModelNode()));

        Assert.assertEquals(3, Iterators.size(taskGroup.iterator()));
    }

    @Test(expected = RuntimeException.class)
    public void unmodifiable() {
        taskGroup.addTask(new Task(new ModelNode()));
        taskGroup.iterator().remove();
    }
}