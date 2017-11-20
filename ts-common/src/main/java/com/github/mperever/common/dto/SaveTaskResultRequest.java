package com.github.mperever.common.dto;

import com.github.mperever.common.utils.ArgumentChecker;

import java.io.Serializable;

/**
 * Represents request to save result of url task processing.
 *
 * @author mperever
 */
public class SaveTaskResultRequest implements Serializable, ErrorKeeper
{
    private static final long serialVersionUID = 1L;

    private String url;

    private String clientId;
    private TaskResults taskResults;
    private Exception error;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private SaveTaskResultRequest()
    {
    }

    public SaveTaskResultRequest( String clientId, String url, Exception error )
    {
        ArgumentChecker.checkNotEmpty( clientId, "clientId" );
        ArgumentChecker.checkNotEmpty( url, "url" );
        ArgumentChecker.checkNotNull( error, "error" );

        this.clientId = clientId;
        this.url = url;
        this.error = error;
    }

    public SaveTaskResultRequest( String clientId, String url, TaskResults taskResults )
    {
        ArgumentChecker.checkNotEmpty( clientId, "clientId" );
        ArgumentChecker.checkNotEmpty( url, "url" );
        ArgumentChecker.checkNotNull( taskResults, "taskResults" );

        this.clientId = clientId;
        this.url = url;
        this.taskResults = taskResults;
    }

    public String getClientId()
    {
        return clientId;
    }

    public String getUrl()
    {
        return url;
    }

    public TaskResults getTaskResults()
    {
        return taskResults;
    }

    public boolean hasError()
    {
        return error != null;
    }

    public Exception getError()
    {
        return error;
    }
}