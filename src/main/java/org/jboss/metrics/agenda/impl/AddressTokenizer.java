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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

/**
 * @author Harald Pehl
 */
public class AddressTokenizer {

    public static AddressTokenizer on(String address) {
        return new AddressTokenizer(address == null ?
                Collections.<String>emptyList() :
                Splitter.on(CharMatcher.anyOf("/="))
                        .trimResults()
                        .omitEmptyStrings()
                        .splitToList(address));
    }

    private final Address address;

    private AddressTokenizer(final List<String> tokens) {
        List<AddressTuple> tuples = new ArrayList<>(tokens.size() / 2 + 1);
        for (Iterator<String> iterator = tokens.iterator(); iterator.hasNext(); ) {
            String type = iterator.next();
            String name = iterator.hasNext() ? iterator.next() : "";
            tuples.add(new AddressTuple(type, name));
        }
        address = new Address(tuples);
    }

    public Address get() {
        return address;
    }
}
