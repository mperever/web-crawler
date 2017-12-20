package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.common.json.JsonSerializer;

import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dto.ErrorKeeper;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.dal.mysql.TaskServiceRepositoryMySql;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.mperever.web.crawler.ts.rest.HttpTaskService_v1.RESOURCE_MEDIA_TYPE;
import static com.github.mperever.web.crawler.ts.rest.HttpTaskService_v1.SERVICE_ROOT_PATH;

/**
 * Represents a servlet for {@link TaskService_v1}.
 * The servlet uses default implementations: {@link TaskService_v1Impl} and {@link TaskServiceRepositoryMySql}.
 *
 * @author mperever
 */
@Path( SERVICE_ROOT_PATH )
@Consumes( RESOURCE_MEDIA_TYPE )
@Produces( RESOURCE_MEDIA_TYPE )
public class HttpTaskService_v1
{
    static final String RESOURCE_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    static final String SERVICE_ROOT_PATH = "/v1";
    static final String TASKS_RETRIEVE_PATH = "tasks.retrieve";
    static final String RESULTS_SAVE_PATH = "results.save";

    private static final Logger logger = LoggerFactory.getLogger( HttpTaskService_v1.class );

    private final TaskService_v1 taskService;
    private final JsonSerializer jsonSerializer;

    /**
     * This constructor should be used only for testing purposes.
     *
     * @param taskService TaskService implementation.
     */
    HttpTaskService_v1( TaskService_v1 taskService )
    {
        this.taskService = taskService;
        jsonSerializer = new JacksonJsonSerializer();
    }

    public HttpTaskService_v1()
    {
        this( new TaskService_v1Impl( new TaskServiceRepositoryMySql() ) );
    }

    @POST
    @Path( TASKS_RETRIEVE_PATH )
    public Response tasksRetrieve( @Context HttpHeaders headers, final RetrieveTasksRequest retrieveRequest )
    {
        try
        {
            final RetrieveTasksResponse response = taskService.retrieveTasks( retrieveRequest );
            final String jsonResponse = jsonSerializer.encode( response );

            return Response.ok( jsonResponse, RESOURCE_MEDIA_TYPE ).build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( new RetrieveTasksResponse( ex ) );
        }
    }

    @POST
    @Path( RESULTS_SAVE_PATH )
    public Response resultsSave( @Context HttpHeaders headers, final SaveTaskResultRequest saveResultRequest )
    {
        try
        {
            final SaveTaskResultResponse response = taskService.saveTaskResults( saveResultRequest );

            if ( response.hasError() )
            {
                return buildExceptionResponse( response );
            }

            final String jsonResponse = jsonSerializer.encode( response );
            return Response.ok( jsonResponse, RESOURCE_MEDIA_TYPE ).build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( new SaveTaskResultResponse( ex ) );
        }
    }

    private Response buildExceptionResponse( final ErrorKeeper responseEntity )
    {
        final Exception error = responseEntity.getError();
        logger.error( error.getMessage(), error );

        final String jsonResponse = jsonSerializer.encode( responseEntity );

        return Response.status( getErrorResponseStatus( error ) )
                .type( RESOURCE_MEDIA_TYPE )
                .entity( jsonResponse )
                .build();
    }

    private Response.Status getErrorResponseStatus( final Exception error )
    {
        final Class errorType = error.getClass();
        if ( errorType.equals( IllegalArgumentException.class ) )
        {
            return Response.Status.BAD_REQUEST;
        }

        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}