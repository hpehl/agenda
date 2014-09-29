package org.jboss.metrics.agenda.impl;

import static org.jboss.metrics.agenda.Interval.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskGroup;
import org.junit.Before;
import org.junit.Test;

public class IntervalGroupingTest {

    private IntervalGrouping grouping;
    @Before
    public void setUp() {
        grouping = new IntervalGrouping();
    }

    @Test
    public void apply() {
        Task s1x = new Task("/a=b", "x", EACH_SECOND);
        Task s1y = new Task("/a=b", "y", EACH_SECOND);
        Task s1z = new Task("/a=b", "z", EACH_SECOND);

        Task s2x = new Task("/a=b", "x", TWO_SECONDS);
        Task s2y = new Task("/a=b", "y", TWO_SECONDS);

        Task m1x = new Task("/a=b", "x", EACH_MINUTE);
        Task m1y = new Task("/a=b", "y", EACH_MINUTE);
        Task m1z = new Task("/a=b", "z", EACH_MINUTE);

        Set<TaskGroup> groups = grouping.apply(Arrays.asList(s1x, s1y, s1z, s2x, s2y, m1x, m1y, m1z));

        assertEquals(3, groups.size());
        assertTrue(groups.contains(new TaskGroup(EACH_SECOND)));
        assertTrue(groups.contains(new TaskGroup(TWO_SECONDS)));
        assertTrue(groups.contains(new TaskGroup(EACH_MINUTE)));
    }
}