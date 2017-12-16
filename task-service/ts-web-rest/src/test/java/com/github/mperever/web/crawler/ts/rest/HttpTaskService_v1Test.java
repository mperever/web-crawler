package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.TaskResults;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;
import com.github.mperever.web.crawler.ts.common.json.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class HttpTaskService_v1Test extends JerseyTestNg.ContainerPerClassTest
{
    private static final String TEST_URL = "www.google.com";
    private static final TaskServiceMock taskService = new TaskServiceMock( new JacksonJsonSerializer() );

    @Path( HttpTaskService_v1.SERVICE_ROOT_PATH )
    @Singleton
    @Consumes( HttpTaskService_v1.RESOURCE_MEDIA_TYPE )
    @Produces( HttpTaskService_v1.RESOURCE_MEDIA_TYPE )
    public static class HttpTaskService_v1TestProxy
    {
        private final HttpTaskService_v1 taskService_v1 = new HttpTaskService_v1( taskService );

        @POST
        @Path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
        public Response tasksRetrieve( @Context HttpHeaders headers, final RetrieveTasksRequest retrieveRequest )
        {
            return taskService_v1.tasksRetrieve( headers, retrieveRequest );
        }

        @POST
        @Path( HttpTaskService_v1.RESULTS_SAVE_PATH )
        public Response resultsSave( @Context HttpHeaders headers, final SaveTaskResultRequest saveResultRequest )
        {
            return taskService_v1.resultsSave( headers, saveResultRequest );
        }
    }

    @Override
    protected Application configure()
    {
        final ResourceConfig config = new ResourceConfig( HttpTaskService_v1TestProxy.class );
        config.register( JacksonObjectMapperContextProvider.class );
        return config;
    }

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

        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
                .request()
                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
        Assert.assertEquals( actualResponse.getTasks(), expectedResponse.getTasks() );
    }

    @Test
    public void tasksRetrieve_should_return_error_if_exception_occurs()
    {
        final String clientId = UUID.randomUUID().toString();
        final int maxCount = 2;
        final int depthLimit = 1;

        final RetrieveTasksRequest request = new RetrieveTasksRequest( clientId, maxCount, depthLimit );

        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
                .request()
                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
        Assert.assertNotNull( actualResponse.getError() );
    }

    @Test
    public void tasksRetrieve_should_return_validation_error_for_missing_request_fields()
    {
        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH )
                .path( HttpTaskService_v1.TASKS_RETRIEVE_PATH )
                .request()
                .post( Entity.entity( null, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode() );
        final RetrieveTasksResponse actualResponse = serviceResponse.readEntity( RetrieveTasksResponse.class );
        final Exception actualError = actualResponse.getError();
        Assert.assertNotNull( actualError );
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

        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
                .request()
                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
        Assert.assertNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_save_error()
    {
        final String clientId = UUID.randomUUID().toString();
        final NotFoundException error = new NotFoundException( "Custom exception" );

        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, error );
        final SaveTaskResultResponse expectedResponse = new SaveTaskResultResponse();
        taskService.addResponse( request.getClientId(), expectedResponse );

        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
                .request()
                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.OK.getStatusCode() );
        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
        Assert.assertNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_return_error_if_exception_occurs()
    {
        final String clientId = UUID.randomUUID().toString();

        final SaveTaskResultRequest request = new SaveTaskResultRequest( clientId, TEST_URL, new TaskResults() );

        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH ).path( HttpTaskService_v1.RESULTS_SAVE_PATH )
                .request()
                .post( Entity.entity( request, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
        Assert.assertNotNull( actualResponse.getError() );
    }

    @Test
    public void resultsSave_should_return_validation_error_for_missing_request_fields()
    {
        final Response serviceResponse = target( HttpTaskService_v1.SERVICE_ROOT_PATH )
                .path( HttpTaskService_v1.RESULTS_SAVE_PATH )
                .request()
                .post( Entity.entity( null, HttpTaskService_v1.RESOURCE_MEDIA_TYPE ) );

        Assert.assertEquals( serviceResponse.getStatus(), Response.Status.BAD_REQUEST.getStatusCode() );
        final SaveTaskResultResponse actualResponse = serviceResponse.readEntity( SaveTaskResultResponse.class );
        final Exception actualError = actualResponse.getError();
        Assert.assertNotNull( actualError );
    }
}