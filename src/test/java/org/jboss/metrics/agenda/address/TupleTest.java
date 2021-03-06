package org.jboss.metrics.agenda.address;

import static org.junit.Assert.assertEquals;

import org.jboss.metrics.agenda.address.Address.Tuple;
import org.junit.Test;

public class TupleTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullTuple() {
        Tuple.apply(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void malformedTuple() {
        Tuple.apply("a=");
    }

    @Test
    public void tuple() {
        Tuple tuple = Tuple.apply("a=b");
        assertEquals(Tuple.apply("a=b"), tuple);
        assertEquals("a", tuple.getKey());
        assertEquals("b", tuple.getValue());
    }
}