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
package org.jboss.metrics.agenda;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jboss.dmr.ModelNode;

/**
 * An executable task holding a {@link org.jboss.dmr.ModelNode} operation.
 *
 * @author Harald Pehl
 */
public class Task {

    private final String id;
    private final ModelNode operation;

    public Task(final ModelNode operation) {
        this.id = UUID.randomUUID().toString();
        this.operation = operation;
    }

    public Task(final Task... tasks) {
        this.id = UUID.randomUUID().toString();
        if (tasks != null) {
            ModelNode comp = new ModelNode();
            List<ModelNode> steps = new ArrayList<>();
            comp.get("address").setEmptyList();
            comp.get("operation").set("composite");
            for (Task task : tasks) {
                steps.add(task.getOperation());
            }
            comp.get("steps").set(steps);
            this.operation = comp;
        } else {
            this.operation = new ModelNode();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Task)) { return false; }

        Task task = (Task) o;

        if (!id.equals(task.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Task(" + id + ": " + operation + ")";
    }

    public String getId() {
        return id;
    }

    public ModelNode getOperation() {
        return operation;
    }
}
