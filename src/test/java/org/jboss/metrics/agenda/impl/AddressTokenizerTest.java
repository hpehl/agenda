package org.jboss.metrics.agenda.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AddressTokenizerTest {

    @Test
    public void nullAddress() {
        Address address = AddressTokenizer.on(null).get();
        assertNotNull(address);
        assertTrue(address.isEmpty());
    }

    @Test
    public void emptyAddress() {
        Address address = AddressTokenizer.on("").get();
        assertNotNull(address);
        assertTrue(address.isEmpty());
    }

    @Test
    public void incompleteAddress() {
        Address address = AddressTokenizer.on("/subsystem=datasources/data-source=").get();
        assertNotNull(address);
        assertFalse(address.isEmpty());
        assertFalse(address.isBalanced());
    }

    @Test
    public void validAddress() {
        Address address = AddressTokenizer.on("/subsystem=datasources/data-source=ExampleDS").get();
        assertNotNull(address);
        assertFalse(address.isEmpty());
        assertTrue(address.isBalanced());
    }
}