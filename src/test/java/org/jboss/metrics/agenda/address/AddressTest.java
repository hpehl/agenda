package org.jboss.metrics.agenda.address;

import static org.junit.Assert.*;

import org.junit.Test;

public class AddressTest {

    @Test
    public void nullAddress() {
        Address address = Address.apply(null);
        assertNotNull(address);
        assertTrue(address.isEmpty());
    }

    @Test
    public void emptyAddress() {
        Address address = Address.apply("");
        assertNotNull(address);
        assertTrue(address.isEmpty());
    }

    @Test
    public void incompleteAddress() {
        Address address = Address.apply("/subsystem=datasources/data-source=");
        assertNotNull(address);
        assertFalse(address.isEmpty());
        assertFalse(address.isBalanced());
    }

    @Test
    public void validAddress() {
        Address address = Address.apply("/subsystem=datasources/data-source=ExampleDS");
        assertNotNull(address);
        assertFalse(address.isEmpty());
        assertTrue(address.isBalanced());
    }
}