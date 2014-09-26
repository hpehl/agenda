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

import java.util.concurrent.TimeUnit;

/**
 * @author Harald Pehl
 */
public enum Interval {

    EACH_SECOND(1, TimeUnit.SECONDS),
    TWO_SECONDS(2, TimeUnit.SECONDS),
    FIVE_SECONDS(5, TimeUnit.SECONDS),

    EACH_MINUTES(1, TimeUnit.MINUTES),
    TWO_MINUTES(2, TimeUnit.MINUTES),
    FIVE_MINUTES(5, TimeUnit.MINUTES),
    TWENTY_MINUTES(20, TimeUnit.MINUTES),

    EACH_HOUR(1, TimeUnit.HOURS);

    private final int val;
    private final TimeUnit unit;

    Interval(int val, TimeUnit unit) {
        this.val = val;
        this.unit = unit;
    }

    public long millis() {
        return TimeUnit.MILLISECONDS.convert(val, unit);
    }
}
