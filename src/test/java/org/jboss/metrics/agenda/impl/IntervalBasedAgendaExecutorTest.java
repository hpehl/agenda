package org.jboss.metrics.agenda.impl;

import static org.junit.Assert.assertEquals;

import org.jboss.metrics.agenda.sample.Agendas;
import org.junit.Before;
import org.junit.Test;

public class IntervalBasedAgendaExecutorTest {

    private IntervalBasedAgendaExecutor executor;

    @Before
    public void setUp() {
        executor = new IntervalBasedAgendaExecutor();
    }

    @Test
    public void prepare() {
        executor.prepare(Agendas.dataSourcePool());
        assertEquals(5, executor.getSchedule().size());
    }
}