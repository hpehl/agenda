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

import static org.jboss.metrics.agenda.Scheduler.State.RUNNING;
import static org.jboss.metrics.agenda.Scheduler.State.STOPPED;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Operation;
import org.jboss.metrics.agenda.OperationResult;
import org.jboss.metrics.agenda.OperationResultConsumer;
import org.jboss.metrics.agenda.Statistics;
import org.jboss.metrics.agenda.TaskGroup;

/**
 * @author Harald Pehl
 */
public class IntervalBasedScheduler extends AbstractScheduler {


    private class DmrOp implements Runnable {

        private final Operation operation;
        private final long interval;

        private DmrOp(final Operation operation, final long interval) {
            this.operation = operation;
            this.interval = interval;
        }

        @Override
        public void run() {
            // TODO Update statistics
            OperationResult operationResult;
            try {
                Timer.Context context = responses.time();
                penalties.inc();
                ModelNode response = client.execute(operation.getOperation());
                long durationMs = context.stop() / 1000000;

                String outcome = response.get("outcome").asString();
                if ("success".equals(outcome)) {
                    if (durationMs < interval) {
                        penalties.dec();
                    }
                    operationResult = new OperationResult(operation.getId(), response.get("result"),
                            OperationResult.Status.SUCCESS);
                } else {
                    operationResult = new OperationResult(operation.getId(), response.get("failure-description"),
                            OperationResult.Status.FAILED);
                }
            } catch (IOException e) {
                ModelNode exceptionModel = new ModelNode().get("failure-description").set(e.getMessage());
                operationResult = new OperationResult(operation.getId(), exceptionModel, OperationResult.Status.FAILED);
            }
            consumer.consume(operationResult);
        }
    }


    public final static int DEFAULT_POOL_SIZE = 2;

    private final ModelControllerClient client;
    private final ScheduledExecutorService executorService;
    private final List<ScheduledFuture> jobs;
    private final OperationResultConsumer consumer;
    private final MetricRegistry metrics;
    private final ConsoleReporter reporter;
    private final Timer responses;
    private final Counter penalties;

    public IntervalBasedScheduler(final ModelControllerClient client) {
        this(client, DEFAULT_POOL_SIZE);
    }

    public IntervalBasedScheduler(final ModelControllerClient client, final int poolSize) {
        this.client = client;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.jobs = new LinkedList<>();
        this.consumer = new PrintOperationResult();
        this.metrics = new MetricRegistry();
        this.reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        this.responses = metrics.timer(MetricRegistry.name(DmrOp.class, "responses"));
        this.penalties = metrics.counter(MetricRegistry.name(DmrOp.class, "penalties"));
    }

    @Override
    public void start(Set<TaskGroup> groups) {
        verifyState(STOPPED);

        ReadAttributeOperationBuilder operationBuilder = new ReadAttributeOperationBuilder();
        for (TaskGroup group : groups) {
            Set<Operation> operations = operationBuilder.createOperation(group);
            for (Operation operation : operations) {
                long millis = group.getInterval().millis();
                jobs.add(executorService.scheduleWithFixedDelay(new DmrOp(operation, millis), 0, millis,
                        TimeUnit.MILLISECONDS));
            }
        }

        pushState(RUNNING);
    }

    @Override
    public void stop() {
        verifyState(RUNNING);

        try {
            for (ScheduledFuture job : jobs) {
                job.cancel(false);
            }
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pushState(STOPPED);
            reporter.stop();
            reporter.report();
        }
    }

    @Override
    public Statistics currentStats() {
        return null;
    }
}
