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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Agenda;
import org.jboss.metrics.agenda.AgendaExecutor;
import org.jboss.metrics.agenda.Statistics;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskDefinition;
import org.jboss.metrics.agenda.TaskResult;

/**
 * @author Harald Pehl
 */
public class IntervalBasedAgendaExecutor implements AgendaExecutor {

    private final Map<Long, IntervalGroup> schedule;
    private final ScheduledExecutorService executorService;
    private ModelControllerClient client;

    public IntervalBasedAgendaExecutor() {
        schedule = new HashMap<>();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

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
            } else {
                // TODO Distribute the DMR operations over multiple composites for large number of operations
                List<Task> tasks = new ArrayList<>();
                for (TaskDefinition definition : definitions) {
                    tasks.add(taskBuilder.createTask(definition));
                }
                taskGroup.addTask(new Task(tasks));
            }
            schedule.put(interval, taskGroup);
        }
    }

    @Override
    public void run(final ModelControllerClient client) {
        for (Map.Entry<Long, IntervalGroup> entry : schedule.entrySet()) {
            Long interval = entry.getKey();
            IntervalGroup taskGroup = entry.getValue();

            List<Runnable> threads = new ArrayList<>();
            for (final Task task : taskGroup) {
                threads.add(new Runnable() {
                    @Override
                    public void run() {
                        System.out.print("Executing " + task);
                        // TODO Exception handling
                        // TODO Extract the code which handles the payload
                        // TODO Update statistics
                        TaskResult taskResult;
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        try {
                            ModelNode response = client.execute(task.getOperation());
                            stopwatch.stop();
                            String outcome = response.get("outcome").asString();
                            if ("success".equals(outcome)) {
                                taskResult = new TaskResult(task.getId(), response.get("result"),
                                        TaskResult.Status.SUCCESS, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                            } else {
                                taskResult = new TaskResult(task.getId(), response.get("failure-description"),
                                        TaskResult.Status.FAILED, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                            }
                        } catch (IOException e) {
                            ModelNode exceptionModel = new ModelNode().get("failure-description").set(e.getMessage());
                            taskResult = new TaskResult(task.getId(), exceptionModel,
                                    TaskResult.Status.FAILED, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                        }
                        System.out.println(": " + taskResult);
                    }
                });
            }

            // Execute an initial DMR op. The initial connection setup should not go into the stats
            ModelNode op = new ModelNode();
            op.get("operation").set("read-attribute");
            op.get("name").set("product-name");
            try {
                client.execute(op);
            } catch (IOException e) {
                // noop
            }

            // TODO Distribute the concurrent execution of tasks in this group across the interval
            for (Runnable thread : threads) {
                executorService.scheduleWithFixedDelay(thread, 0, interval, MILLISECONDS);
            }
            this.client = client;
        }
    }

    @Override
    public void shutdown() throws InterruptedException, IOException {
        if (client != null) {
            client.close();
        }
        executorService.shutdownNow();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Override
    public Statistics currentStats() {
        return null;
    }

    protected Map<Long, IntervalGroup> getSchedule() {
        return Collections.unmodifiableMap(schedule);
    }
}
