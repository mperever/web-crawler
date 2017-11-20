package com.github.mperever.common.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;

/**
 * Represents response for {@link RetrieveTasksRequest}.
 *
 * @author mperever
 */
@SuppressFBWarnings( { "EI_EXPOSE_REP" } )
public class RetrieveTasksResponse implements Serializable, ErrorKeeper
{
    private static final long serialVersionUID = 1L;

    private Exception error;
    private UrlTask[] tasks;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private RetrieveTasksResponse()
    {
    }

    public RetrieveTasksResponse( Exception error )
    {
        this.error = error;
    }

    public RetrieveTasksResponse( UrlTask... tasks )
    {
        this.tasks = tasks;
    }

    public boolean hasError()
    {
        return error != null;
    }

    public Exception getError()
    {
        return error;
    }

    public UrlTask[] getTasks()
    {
        return tasks;
    }
}