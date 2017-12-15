package com.github.mperever.common.dto;

import java.io.Serializable;

/**
 * Represents request to retrieve url tasks for processing.
 *
 * @author mperever
 */
public class RetrieveTasksRequest implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String clientId;
    private int maxCount;
    private int depthLimit;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private RetrieveTasksRequest()
    {
    }

    public RetrieveTasksRequest( String clientId, int maxCount, int depthLimit )
    {
        this.clientId = clientId;
        this.maxCount = maxCount;
        this.depthLimit = depthLimit;
    }

    public String getClientId()
    {
        return clientId;
    }

    public int getMaxCount()
    {
        return maxCount;
    }

    public int getDepthLimit()
    {
        return depthLimit;
    }
}