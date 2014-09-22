/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.metrics.agenda.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * @author Harald Pehl
 */
public class Address implements Iterable<AddressTuple> {

    private final List<AddressTuple> tuples;

    public Address() {
        this(Collections.<AddressTuple>emptyList());
    }

    public Address(final List<AddressTuple> tuples) {
        this.tuples = new ArrayList<>();
        if (tuples != null) {
            this.tuples.addAll(tuples);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Address)) { return false; }

        Address address = (Address) o;

        if (!tuples.equals(address.tuples)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return tuples.hashCode();
    }

    @Override
    public String toString() {
        return Joiner.on('/').join(tuples);
    }

    @Override
    public Iterator<AddressTuple> iterator() {
        return tuples.iterator();
    }

    public boolean isEmpty() {return tuples.isEmpty();}

    public boolean isBalanced() {
        for (AddressTuple tuple : this) {
            if (tuple.getValue() == null || tuple.getValue().length() == 0) {
                return false;
            }
        }
        return true;
    }
}
