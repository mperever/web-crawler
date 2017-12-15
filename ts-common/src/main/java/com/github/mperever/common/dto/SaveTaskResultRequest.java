package com.github.mperever.common.dto;

/**
 * Represents request to save result of url task processing.
 *
 * @author mperever
 */
public class SaveTaskResultRequest implements ErrorKeeper
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
        this.clientId = clientId;
        this.url = url;
        this.error = error;
    }

    public SaveTaskResultRequest( String clientId, String url, TaskResults taskResults )
    {
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