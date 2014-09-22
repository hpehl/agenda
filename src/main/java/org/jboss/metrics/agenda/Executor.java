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

/**
 * An interface which takes an {@link org.jboss.metrics.agenda.Agenda}, turns it into
 * executable {@link org.jboss.metrics.agenda.Task}s and executes them repeatedly.
 * <p/>
 * This interface has an implicit lifecycle:
 * <ol>
 * <li>Prepare: Takes an agenda, transforms it to executable tasks.</li>
 * <li>Running: Executes the given tasks. How the tasks are executed and in which order highly depends on the
 * concrete implementation.</li>
 * <li>Shut down: Stops the execution of the tasks managed by this executor.</li>
 * </ol>
 *
 * @author Harald Pehl
 */
public interface Executor {

    void prepare(Agenda agenda);

    void run();

    void shutdown();

    /**
     * Returns the current statistics. This method should not block the executor and run very fast.
     */
    Statistics currentStats();
}
