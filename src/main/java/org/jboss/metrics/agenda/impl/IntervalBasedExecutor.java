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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.jboss.metrics.agenda.Agenda;
import org.jboss.metrics.agenda.Executor;
import org.jboss.metrics.agenda.Statistics;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskDefinition;
import org.jboss.metrics.agenda.TaskGroup;

/**
 * @author Harald Pehl
 */
public class IntervalBasedExecutor implements Executor {

    final Map<Long, TaskGroup> schedule;

    public IntervalBasedExecutor() {schedule = new HashMap<>();}

    @Override
    public void prepare(final Agenda agenda) {
        // group by interval
        ImmutableListMultimap<Long, TaskDefinition> byInterval = Multimaps
                .index(agenda, new Function<TaskDefinition, Long>() {
                    @Override
                    public Long apply(final TaskDefinition definition) {
                        return definition.getIntervalInMillis();
                    }
                });

        // create composite operations from task definitions with the same interval
        ReadAttributeTaskBuilder taskBuilder = new ReadAttributeTaskBuilder();
        for (Long interval : byInterval.keys()) {
            IntervalGroup taskGroup = new IntervalGroup(interval);
            ImmutableList<TaskDefinition> definitions = byInterval.get(interval);
            if (definitions.size() == 1) {
                Task task = taskBuilder.createTask(definitions.get(0));
                taskGroup.addTask(task);
            }
            else {
                List<Task> tasks = new ArrayList<>();
                for (TaskDefinition definition : definitions) {
                    tasks.add(taskBuilder.createTask(definition));
                }
                taskGroup.addTask(new Task(tasks.toArray(new Task[tasks.size()])));
            }
            schedule.put(interval, taskGroup);
        }
    }

    @Override
    public void run() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public Statistics currentStats() {
        return null;
    }
}
