package org.jboss.metrics.agenda;

import static java.util.Arrays.asList;

import org.junit.Test;

public class AgendaTest {

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        Agenda agenda = new Agenda("test", asList(TestData.fooTask()));
        agenda.getTasks().remove(0);
    }
}