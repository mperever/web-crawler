package com.github.mperever.rest;

import com.github.mperever.common.TaskService_v1;
import com.github.mperever.common.dto.ErrorKeeper;
import com.github.mperever.common.dto.RetrieveTasksRequest;
import com.github.mperever.common.dto.RetrieveTasksResponse;
import com.github.mperever.common.dto.SaveTaskResultRequest;
import com.github.mperever.common.dto.SaveTaskResultResponse;
import com.github.mperever.common.json.JacksonJsonSerializer;
import com.github.mperever.common.json.JsonSerializer;
import com.github.mperever.dal.mysql.TaskServiceRepositoryMySql;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a servlet for {@link TaskService_v1}.
 * The servlet uses default implementations: {@link TaskService_v1Impl} and {@link TaskServiceRepositoryMySql}.
 */
@Path( "/v1" )
public class HttpTaskService_v1
{
    private static final Logger logger = LoggerFactory.getLogger( HttpTaskService_v1.class );

    private static final String RESOURCE_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private final static String REQUEST_BODY_MISSING_TEMPLATE = "Could not %s to request body is not specified";

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
        this.jsonSerializer = new JacksonJsonSerializer();
    }

    public HttpTaskService_v1()
    {
        this( new TaskService_v1Impl( new TaskServiceRepositoryMySql() ) );
    }

    @POST
    @Path( "tasks.retrieve" )
    @Consumes( RESOURCE_MEDIA_TYPE )
    @Produces( RESOURCE_MEDIA_TYPE )
    public Response tasksRetrieve( final String jsonRetrieveRequest )
    {
        if ( jsonRetrieveRequest == null || jsonRetrieveRequest.isEmpty() )
        {
            return this.buildRetrieveErrorParameterResponse(
                    String.format( REQUEST_BODY_MISSING_TEMPLATE, "retrieve tasks" ) );
        }

        try
        {
            final RetrieveTasksRequest request = jsonSerializer.decode(
                    jsonRetrieveRequest,
                    RetrieveTasksRequest.class );

            final RetrieveTasksResponse response = taskService.retrieveTasks( request );
            final String jsonResponse = jsonSerializer.encode( response );

            return Response.ok( jsonResponse, RESOURCE_MEDIA_TYPE ).build();

        } catch ( Exception ex )
        {
            return buildExceptionResponse( new RetrieveTasksResponse( ex ) );
        }
    }

    @POST
    @Path( "results.save" )
    @Consumes( RESOURCE_MEDIA_TYPE )
    @Produces( RESOURCE_MEDIA_TYPE )
    public Response resultsSave( final String jsonSaveResultRequest )
    {
        if ( jsonSaveResultRequest == null || jsonSaveResultRequest.isEmpty() )
        {
            return this.buildSaveResultsErrorParameterResponse(
                    String.format( REQUEST_BODY_MISSING_TEMPLATE, "save task results" ) );
        }

        try
        {
            final SaveTaskResultRequest request = this.jsonSerializer.decode(
                    jsonSaveResultRequest,
                    SaveTaskResultRequest.class );

            final SaveTaskResultResponse response = taskService.saveTaskResults( request );

            if ( response.hasError() )
            {
                return buildExceptionResponse( response );
            }

            final String jsonResponse = jsonSerializer.encode( response );
            return Response.ok( jsonResponse, RESOURCE_MEDIA_TYPE ).build();

        } catch ( Exception ex )
        {
            return buildExceptionResponse( new SaveTaskResultResponse( ex ) );
        }
    }

    private Response buildSaveResultsErrorParameterResponse( final String message )
    {
        return buildErrorParameterResponse( new SaveTaskResultResponse(
                new IllegalArgumentException( message ) ) );
    }

    private Response buildRetrieveErrorParameterResponse( final String message )
    {
        return buildErrorParameterResponse( new RetrieveTasksResponse(
                new IllegalArgumentException( message ) ) );
    }

    private Response buildErrorParameterResponse( final ErrorKeeper responseEntity )
    {
        logger.error( responseEntity.getError().getMessage() );

        final String jsonResponse = this.jsonSerializer.encode( responseEntity );
        return Response.status( Response.Status.BAD_REQUEST )
                .type( RESOURCE_MEDIA_TYPE )
                .entity( jsonResponse ).build();
    }

    private Response buildExceptionResponse( final ErrorKeeper responseEntity )
    {
        logger.error( responseEntity.getError().getMessage() );

        final String jsonResponse = this.jsonSerializer.encode( responseEntity );
        return Response.serverError()
                .type( RESOURCE_MEDIA_TYPE )
                .entity( jsonResponse )
                .build();
    }
}