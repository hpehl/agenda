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

import static org.jboss.metrics.agenda.TaskDefinition.TimeUnit.SECOND;

/**
 * @author Harald Pehl
 */
public class TaskDefinition {

    public enum TimeUnit {SECOND, MINUTE, HOUR}

    private final String address;
    private final String attribute;
    private final int interval;
    private final TimeUnit unit;

    public TaskDefinition(final String address, final String attribute, final int interval) {
        this(address, attribute, interval, SECOND);
    }

    public TaskDefinition(final String address, final String attribute, final int interval,
            final TimeUnit unit) {
        this.address = address;
        this.attribute = attribute;
        this.interval = interval;
        this.unit = unit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TaskDefinition)) { return false; }

        TaskDefinition that = (TaskDefinition) o;

        if (interval != that.interval) { return false; }
        if (!address.equals(that.address)) { return false; }
        if (!attribute.equals(that.attribute)) { return false; }
        if (unit != that.unit) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + attribute.hashCode();
        result = 31 * result + interval;
        result = 31 * result + unit.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TaskDefinition(" + address + ":" + attribute + " every " + interval + " " + unit.name().toLowerCase() + "(s)";
    }

    public String getAddress() {
        return address;
    }

    public String getAttribute() {
        return attribute;
    }

    public int getInterval() {
        return interval;
    }

    public long getIntervalInMillis() {
        final long ms;
        switch (unit) {
            case SECOND:
                ms = interval * 1000;
                break;
            case MINUTE:
                ms = interval * 60 * 1000;
                break;
            case HOUR:
                ms = interval * 60 * 60 * 1000;
                break;
            default:
                ms = 0;
                break;
        }
        return ms;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
