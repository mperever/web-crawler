package com.github.mperever.rest;

import com.github.mperever.common.TaskService_v1;
import com.github.mperever.common.dto.RetrieveTasksRequest;
import com.github.mperever.common.dto.RetrieveTasksResponse;
import com.github.mperever.common.dto.SaveTaskResultRequest;
import com.github.mperever.common.dto.SaveTaskResultResponse;

public class TaskServiceMock implements TaskService_v1
{
    private RetrieveTasksRequest retrieveRequest;
    private RetrieveTasksResponse retrieveResponse;
    private SaveTaskResultRequest saveResultsRequest;
    private SaveTaskResultResponse saveResultsResponse;

    public void seStubForRetrieveTasks( RetrieveTasksRequest request, RetrieveTasksResponse response )
    {
        retrieveRequest = request;
        retrieveResponse = response;
    }

    @Override
    public RetrieveTasksResponse retrieveTasks( RetrieveTasksRequest request )
    {
        return request != null && request.getClientId().equals( retrieveRequest.getClientId() )
                ? retrieveResponse
                : null;
    }

    public void setSaveResultsStub( SaveTaskResultRequest request, SaveTaskResultResponse response )
    {
        saveResultsRequest = request;
        saveResultsResponse = response;
    }

    @Override
    public SaveTaskResultResponse saveTaskResults( SaveTaskResultRequest request )
    {
        return request != null && request.getUrl().equals( saveResultsRequest.getUrl())
                ? saveResultsResponse
                : null;
    }
}