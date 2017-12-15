package com.github.mperever.common.dal;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.github.mperever.common.dto.UrlTask;

/**
 * Represents a container with task results for persistence.
 *
 * @author mperever
 */
@SuppressFBWarnings( { "EI_EXPOSE_REP" } )
public class TaskResultEntities
{
    private int taskId;

    private UrlTask[] tasks;
    private TaskPageTextStats stats;

    public TaskResultEntities( int taskId )
    {
        this.taskId = taskId;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public UrlTask[] getTasks()
    {
        return tasks;
    }

    public void setTasks( UrlTask... tasks )
    {
        this.tasks = tasks;
    }

    public TaskPageTextStats getStats()
    {
        return stats;
    }

    public void setStats( TaskPageTextStats stats )
    {
        this.stats = stats;
    }
}