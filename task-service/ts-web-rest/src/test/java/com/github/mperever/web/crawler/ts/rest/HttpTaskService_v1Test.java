package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.TaskResults;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import org.testng.annotations.Test;

// TODO: Write tests for business logic instead of http. Get rid of JerseyTestNg tests

public class HttpTaskService_v1Test
{
    private static final String TEST_URL = "www.google.com";
    private static final TaskServiceMock taskService = new TaskServiceMock( new JacksonJsonSerializer() );

    @Test
    public void tasksRetrieve_should_return_tasks()
    {
        final String clientId = UUID.randomUUID().toString();
        final int maxCount = 2;
        final int depthLimit = 1;

        final RetrieveTasksRequest request = new RetrieveTasksRequest( clientId, maxCount, depthLimit );
        final UrlTask task = new UrlTask( null, TEST_URL, 1, true );
        final RetrieveTasksResponse expectedResponse = new RetrieveTasksResponse( task );
        taskService.addResponse( request.getClientId(), expectedResponse );

//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
//                .request()
//                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
//        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
//        Assert.assertEquals( actualResponse.getTasks(), expectedResponse.getTasks() );
    }

    @Test
    public void tasksRetrieve_should_return_error_if_exception_occurs()
    {
        final String clientId = UUID.randomUUID().toString();
        final int maxCount = 2;
        final int depthLimit = 1;

        final RetrieveTasksRequest request = new RetrieveTasksRequest( clientId, maxCount, depthLimit );

//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
//                .request()
//                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
//        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
//        Assert.assertNotNull( actualResponse.getError() );
    }

    @Test
    public void tasksRetrieve_should_return_validation_error_for_missing_request_fields()
    {
//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH )
//                .path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
//                .request()
//                .post( Entity.entity( null, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode() );
//        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
//        final Exception actualError = actualResponse.getError();
//        Assert.assertNotNull( actualError );
    }

    @Test
    public void resultsSave_should_save_task_results()
    {
        final String clientId = UUID.randomUUID().toString();

        final TaskResults taskResults = new TaskResults();
        taskResults.setPageText( "Some page text" );
        final Map<String, Long> wordStats = new HashMap<>();
        wordStats.put( "a", 1L );
        taskResults.setWordsStats( wordStats );
        taskResults.setNewUrls( new String[] { "new_url1" } );

        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, taskResults);
        final SaveTaskResultResponse expectedResponse = new SaveTaskResultResponse();
        taskService.addResponse( request.getClientId(), expectedResponse );

//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
//                .request()
//                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
//        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
//        Assert.assertNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_save_error()
    {
        final String clientId = UUID.randomUUID().toString();
        final NotFoundException error = new NotFoundException( "Custom exception" );

        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, error );
        final SaveTaskResultResponse expectedResponse = new SaveTaskResultResponse();
        taskService.addResponse( request.getClientId(), expectedResponse );

//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
//                .request()
//                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
//        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
//        Assert.assertNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_return_error_if_exception_occurs()
    {
        final String clientId = UUID.randomUUID().toString();

        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, new TaskResults() );

//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
//                .request()
//                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
//        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
//        Assert.assertNotNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_return_validation_error_for_missing_request_fields()
    {
//        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH )
//                .path( HttpTaskService_v1.RESULTS_SAVE_PATH )
//                .request()
//                .post( Entity.entity( null, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );
//
//        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode() );
//        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
//        final Exception actualError = actualResponse.getError();
//        Assert.assertNotNull( actualError );
    }
}