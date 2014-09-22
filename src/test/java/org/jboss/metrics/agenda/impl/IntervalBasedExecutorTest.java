package org.jboss.metrics.agenda.impl;

import static org.junit.Assert.assertEquals;

import org.jboss.metrics.agenda.sample.Agendas;
import org.junit.Before;
import org.junit.Test;

public class IntervalBasedExecutorTest {

    private IntervalBasedExecutor executor;

    @Before
    public void setUp() {
        executor = new IntervalBasedExecutor();
    }

    @Test
    public void prepare() {
        executor.prepare(Agendas.dataSourcePool());
        assertEquals(5, executor.schedule.size());
    }
}