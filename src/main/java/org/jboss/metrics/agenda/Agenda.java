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
import java.util.Collections;
import java.util.List;

/**
 * A collection of {@link Task}s with a unique id.
 *
 * @author Harald Pehl
 */
public class Agenda {

    private final String id;
    private final List<Task> tasks;

    public Agenda(final String id, final List<Task> tasks) {
        this.id = id;
        this.tasks = new ArrayList<>();

        if (tasks != null) {
            this.tasks.addAll(tasks);
        }
    }

    public String getId() {
        return id;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
