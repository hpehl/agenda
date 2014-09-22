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

import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskBuilder;
import org.jboss.metrics.agenda.TaskDefinition;

/**
 * Creates a {@code read-attribute} operation of the given {@link org.jboss.metrics.agenda.TaskDefinition}.
 * @author Harald Pehl
 */
public class ReadAttributeTaskBuilder implements TaskBuilder {

    @Override
    public Task createTask(final TaskDefinition definition) {
        ModelNode op = new ModelNode();

        Address address = AddressTokenizer.on(definition.getAddress()).get();
        for (AddressTuple tuple : address) {
            op.get("address").add(tuple.getKey(), tuple.getValue());
        }
        op.get("operation").set("read-attribute");
        op.get("name").set(definition.getAttribute());
        return new Task(op);
    }
}
