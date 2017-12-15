package com.github.mperever.web.crawler.ts.common.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents response for {@link RetrieveTasksRequest}.
 *
 * @author mperever
 */
@SuppressFBWarnings( { "EI_EXPOSE_REP" } )
public class RetrieveTasksResponse implements ErrorKeeper
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


    public UrlTask[] getTasks()
    {
        return tasks;
    }

    @Override
    public boolean hasError()
    {
        return error != null;
    }

    @Override
    public Exception getError()
    {
        return error;
    }
}