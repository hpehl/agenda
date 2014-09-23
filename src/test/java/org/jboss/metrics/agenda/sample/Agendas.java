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
package org.jboss.metrics.agenda.sample;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.jboss.metrics.agenda.Agenda;
import org.jboss.metrics.agenda.TaskDefinition;

/**
 * @author Harald Pehl
 */
public final class Agendas {

    private Agendas() {}

    public static Agenda dataSourcePool() {
        List<TaskDefinition> definitions = new ArrayList<>();
        String address = "/subsystem=datasources/data-source=ExampleDS/statistics=pool";

        // 5
        definitions.add(new TaskDefinition(address, "CreatedCount", 5, SECONDS));
        definitions.add(new TaskDefinition(address, "DestroyedCount", 5, SECONDS));

        // 4
        definitions.add(new TaskDefinition(address, "TimedOut", 4, SECONDS));

        // 3
        definitions.add(new TaskDefinition(address, "InUseCount", 3, SECONDS));

        // 2
        definitions.add(new TaskDefinition(address, "AverageBlockingTime", 2, SECONDS));
        definitions.add(new TaskDefinition(address, "AverageCreationTime", 2, SECONDS));

        // 1
        definitions.add(new TaskDefinition(address, "AvailableCount", 1, SECONDS));
        definitions.add(new TaskDefinition(address, "ActiveCount", 1, SECONDS));

        return new Agenda("dataSourcePool", definitions);
    }
}
