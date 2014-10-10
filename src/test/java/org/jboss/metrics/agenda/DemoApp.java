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

import static java.util.concurrent.TimeUnit.SECONDS;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.metrics.agenda.impl.IntervalBasedScheduler;
import org.jboss.metrics.agenda.impl.IntervalGrouping;
import org.jboss.metrics.agenda.impl.PrintOperationResult;
import org.jboss.metrics.agenda.impl.ReadAttributeOperationBuilder;

/**
 * @author Harald Pehl
 */
public class DemoApp {

    public static void main(String[] args) throws Exception {

        Agenda agenda = TestData.loadTestAgenda();
        Set<TaskGroup> groups = new IntervalGrouping().apply(agenda.getTasks());
        Set<Operation> operations = new HashSet<>();

        ReadAttributeOperationBuilder operationBuilder = new ReadAttributeOperationBuilder();
        for (TaskGroup group : groups) {
            operations.addAll(operationBuilder.createOperation(group));
        }

        System.out.println("<< Agenda Size: "+agenda.getTasks().size()+" >>");
        System.out.println("<< Number of Task Groups: "+groups.size()+" >>");
        System.out.println("<< Number of Operations: "+operations.size()+" >>");

        ModelControllerClient client = ModelControllerClient.Factory.create(InetAddress.getByName("localhost"), 9999);
        Scheduler executor = new IntervalBasedScheduler(client, 5, new PrintOperationResult());
        executor.start(operations);
        SECONDS.sleep(10);
        executor.stop();
    }
}
