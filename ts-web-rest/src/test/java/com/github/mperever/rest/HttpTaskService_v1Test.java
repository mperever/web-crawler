package com.github.mperever.rest;

import com.github.mperever.common.dto.RetrieveTasksRequest;
import com.github.mperever.common.dto.RetrieveTasksResponse;
import com.github.mperever.common.dto.SaveTaskResultRequest;
import com.github.mperever.common.dto.SaveTaskResultResponse;
import com.github.mperever.common.dto.TaskResults;
import com.github.mperever.common.dto.UrlTask;
import com.github.mperever.common.json.JacksonJsonSerializer;
import com.github.mperever.common.json.JsonSerializer;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HttpTaskService_v1Test
{
    private static final String TEST_URL = "www.google.com";
    private final JsonSerializer serializer = new JacksonJsonSerializer();

    @Test
    public void retrieve_tasks_should_return_tasks_for_client()
    {
        // Setup mock and test input data
        final String clientId = UUID.randomUUID().toString();
        final int maxCount = 2;
        final int depthLimit = 1;
        final RetrieveTasksRequest retrieveRequest = new RetrieveTasksRequest( clientId, maxCount, depthLimit );

        final UrlTask task = new UrlTask( null, TEST_URL, 1, true );
        final RetrieveTasksResponse retrieveResponse = new RetrieveTasksResponse( task );

        final TaskServiceMock taskService = new TaskServiceMock();
        taskService.seStubForRetrieveTasks( retrieveRequest, retrieveResponse );

        // Test get method
        final HttpTaskService_v1 httpTaskService = new HttpTaskService_v1( taskService );
        final Response response = httpTaskService.tasksRetrieve( serializer.encode( retrieveRequest ) );

        // Assert response
        this.assertOkResponse( response );
    }

    @Test
    public void retrieve_tasks_should_fail_for_empty_body()
    {
        // Test get method
        final HttpTaskService_v1 httpTaskService = new HttpTaskService_v1( new TaskServiceMock() );
        final Response response = httpTaskService.tasksRetrieve( null );

        // Assert response
        this.assertBadRequestResponse( response );
    }

    @Test
    public void saveTaskResults_should_save_results()
    {
        // Setup mock and test input data
        final String clientId = UUID.randomUUID().toString();
        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, new TaskResults() );

        final TaskServiceMock taskService = new TaskServiceMock();
        taskService.setSaveResultsStub( request, new SaveTaskResultResponse() );

        // Test add method
        final HttpTaskService_v1 httpTaskService = new HttpTaskService_v1( taskService );
        final Response response = httpTaskService.resultsSave( serializer.encode( request ) );

        // Assert response
        this.assertOkResponse( response );
    }

    @Test
    public void saveTaskResults_should_fail_for_empty_body()
    {
        // Test get method
        final HttpTaskService_v1 httpTaskService = new HttpTaskService_v1( new TaskServiceMock() );
        final Response response = httpTaskService.resultsSave( null );

        // Assert response
        this.assertBadRequestResponse( response );
    }

    private void assertOkResponse( final Response response )
    {
        Assert.assertNotNull( response, "Fail. Response is null" );
        Assert.assertEquals( response.getStatus(), Response.Status.OK.getStatusCode(),
                "Fail. Response status code" );
        final String actualResponseBody = response.getEntity().toString();
        Assert.assertNotNull( actualResponseBody, "Fail. Response body is null" );
    }

    private void assertBadRequestResponse( final Response response )
    {
        Assert.assertNotNull( response, "Fail. Response is null" );
        Assert.assertEquals( response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode(),
                "Fail. Response status code" );
    }
}