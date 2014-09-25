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
import static org.jboss.metrics.agenda.AgendaExecutor.State.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Agenda;
import org.jboss.metrics.agenda.TaskResultConsumer;
import org.jboss.metrics.agenda.Statistics;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskDefinition;
import org.jboss.metrics.agenda.TaskResult;

/**
 * @author Harald Pehl
 */
public class IntervalBasedAgendaExecutor extends AbstractAgendaExecutor {

    private class DmrOp implements Runnable {

        private final Task task;

        private DmrOp(final Task task) {
            this.task = task;
        }

        @Override
        public void run() {
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
            consumer.consume(taskResult);
        }
    }


    public final static int DEFAULT_POOL_SIZE = 2;

    private final ScheduledExecutorService executorService;
    private final Map<Long, Task> tasksByInterval;
    private final List<ScheduledFuture> jobs;
    private final TaskResultConsumer consumer;
    private ModelControllerClient client;

    public IntervalBasedAgendaExecutor() {
        this(DEFAULT_POOL_SIZE);
    }

    public IntervalBasedAgendaExecutor(final int poolSize) {
        this.executorService = Executors.newScheduledThreadPool(poolSize);
        this.tasksByInterval = new HashMap<>();
        this.jobs = new LinkedList<>();
        this.consumer = new PrintTaskResult();
    }

    @Override
    public void prepare(final Agenda agenda) {
        verifyState(SHUT_DOWN);

        // group task definitions by interval
        ImmutableListMultimap<Long, TaskDefinition> definitionsByInterval = Multimaps
                .index(agenda, new Function<TaskDefinition, Long>() {
                    @Override
                    public Long apply(final TaskDefinition definition) {
                        return definition.getIntervalInMillis();
                    }
                });

        // create (composite) tasks from definitions with the same interval
        final ReadAttributeTaskBuilder taskBuilder = new ReadAttributeTaskBuilder();
        for (Long interval : definitionsByInterval.keys()) {
            final Task task;
            ImmutableList<TaskDefinition> definitions = definitionsByInterval.get(interval);
            if (definitions.size() == 1) {
                task = taskBuilder.createTask(definitions.get(0));
            } else {
                // TODO Distribute the DMR operations over multiple composites if number of definitions > threshold
                List<Task> tasks = Lists.transform(definitions, new Function<TaskDefinition, Task>() {
                    @Override
                    public Task apply(final TaskDefinition definition) {
                        return taskBuilder.createTask(definition);
                    }
                });
                task = new Task(tasks);
            }

            // schedule the DMR operations
            tasksByInterval.put(interval, task);
        }

        pushState(PREPARED);
    }

    @Override
    public void run(final ModelControllerClient client) {
        verifyState(PREPARED);

        // Execute an initial DMR op: The initial connection setup should not go into the stats
        ModelNode op = new ModelNode();
        op.get("operation").set("read-attribute");
        op.get("name").set("product-name");
        try {
            client.execute(op);
        } catch (IOException e) {
            // noop
        }

        this.client = client;
        for (Map.Entry<Long, Task> entry : tasksByInterval.entrySet()) {
            long interval = entry.getKey();
            Task task = entry.getValue();
            jobs.add(executorService.scheduleWithFixedDelay(new DmrOp(task), 0, interval, MILLISECONDS));
        }

        pushState(RUNNING);
    }

    @Override
    public void shutdown() throws InterruptedException, IOException {
        verifyState(RUNNING);

        try {
            for (ScheduledFuture job : jobs) {
                job.cancel(false);
            }
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } finally {
            if (client != null) {
                client.close();
            }
            pushState(SHUT_DOWN);
        }
    }

    @Override
    public Statistics currentStats() {
        return null;
    }
}
