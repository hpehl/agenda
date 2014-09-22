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

import org.jboss.dmr.ModelNode;

/**
 * @author Harald Pehl
 */
public class TaskResult {

    public enum Status { SUCCESS, FAILED, EXCEPTION}

    private final String taskId;
    private final ModelNode result;
    private final Status status;
    private final long duration;

    public TaskResult(final String taskId, final ModelNode result, final Status status, final long duration) {
        this.taskId = taskId;
        this.duration = duration;
        this.result = result;
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TaskResult)) { return false; }

        TaskResult that = (TaskResult) o;

        if (!result.equals(that.result)) { return false; }
        if (status != that.status) { return false; }
        if (!taskId.equals(that.taskId)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = taskId.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + status.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return "TaskResult(" + taskId + ", " + status + ", " + duration + "ms)";
    }

    public String getTaskId() {
        return taskId;
    }

    public ModelNode getResult() {
        return result;
    }

    public String getErrorDescription() {
        if (status == Status.FAILED || status == Status.EXCEPTION) {
            return result.get("failure-description").asString();
        }
        return null;
    }

    public Status getStatus() {
        return status;
    }

    public long getDuration() {
        return duration;
    }
}
