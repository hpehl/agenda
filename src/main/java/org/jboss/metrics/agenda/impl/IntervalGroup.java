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

import static com.google.common.collect.Multimaps.unmodifiableMultimap;

import java.util.Comparator;
import java.util.Iterator;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskGroup;

/**
 * Groups tasks by interval and uses a priority for the order of tasks.
 *
 * @author Harald Pehl
 */
public class IntervalGroup implements TaskGroup {

    private static final int INITIAL_PRIORITY = 0;

    private final long interval; // in ms
    private final Multimap<Integer, Task> tasks; // by priority

    public IntervalGroup(final long interval) {
        this.interval = interval;
        // Please note: Ordering.arbitrary() is *not* serializable!
        this.tasks = TreeMultimap.create(Ordering.natural(), new Comparator<Task>() {
            @Override
            public int compare(final Task t1, final Task t2) {
                return t1.getId().compareTo(t2.getId());
            }
        });
    }

    public void addTask(Task task) {
        tasks.put(INITIAL_PRIORITY, task);
    }

    @Override
    public Iterator<Task> iterator() {
        return unmodifiableMultimap(tasks).values().iterator();
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "IntervalGroup(" + interval + "ms: " + tasks + ")";
    }
}
