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

import static org.jboss.metrics.agenda.Interval.EACH_SECOND;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harald Pehl
 */
public final class TestData {

    private TestData() {}

    public static Task fooTask() {
        return fooTask(EACH_SECOND);
    }

    public static Task fooTask(Interval interval) {
        return new Task("/foo=bar", "baz", interval);
    }

    public static Agenda dataSourceAgenda() {
        List<Task> definitions = new ArrayList<>();
        String address = "/subsystem=datasources/data-source=ExampleDS/statistics=pool";

        definitions.add(new Task(address, "CreatedCount", EACH_SECOND));
        definitions.add(new Task(address, "DestroyedCount", EACH_SECOND));

        definitions.add(new Task(address, "TimedOut", Interval.TWO_SECONDS));
        definitions.add(new Task(address, "InUseCount", Interval.TWO_SECONDS));
        definitions.add(new Task(address, "AverageBlockingTime", Interval.TWO_SECONDS));

        definitions.add(new Task(address, "AverageCreationTime", Interval.FIVE_SECONDS));
        definitions.add(new Task(address, "AvailableCount", Interval.FIVE_SECONDS));
        definitions.add(new Task(address, "ActiveCount", Interval.FIVE_SECONDS));

        return new Agenda("dataSourceAgenda", definitions);
    }

    public static Agenda loadTestAgenda() {
        List<Task> definitions = new ArrayList<>();


        for(int i=0; i<250; i++) {
            String address = "/subsystem=datasources/data-source=ExampleDS/statistics=pool";

            definitions.add(new Task(address, "CreatedCount", EACH_SECOND));
            definitions.add(new Task(address, "DestroyedCount", EACH_SECOND));

            definitions.add(new Task(address, "TimedOut", Interval.TWO_SECONDS));
            definitions.add(new Task(address, "InUseCount", Interval.TWO_SECONDS));
            definitions.add(new Task(address, "AverageBlockingTime", Interval.TWO_SECONDS));

            definitions.add(new Task(address, "AverageCreationTime", Interval.FIVE_SECONDS));
            definitions.add(new Task(address, "AvailableCount", Interval.FIVE_SECONDS));
            definitions.add(new Task(address, "ActiveCount", Interval.FIVE_SECONDS));
        }

        return new Agenda("dataSourceAgenda", definitions);
    }
}
