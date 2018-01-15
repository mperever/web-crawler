package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.common.rest.HttpService;
import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dto.ErrorKeeper;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;
import com.github.mperever.web.crawler.ts.dal.mysql.TaskServiceRepositoryMySql;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
public class HttpTaskService_v1 extends HttpService
{
    private static final Logger logger = LoggerFactory.getLogger( HttpTaskService_v1.class );

    static final String SERVICE_ROOT_PATH = "/v1";

    private static final String TASKS_RETRIEVE_PATH = "tasks.retrieve";
    private static final String RESULTS_SAVE_PATH = "results.save";
    private static final String ADD_TASK_PATH = "add.task";
    private static final String GET_TASKS = "get.tasks";
    private static final String GET_TASKS_OFFSET_PARAM = "offset";
    private static final String GET_TASKS_LIMIT_PARAM = "limit";

    private final TaskService_v1 taskService;

    /**
     * This constructor should be used only for testing purposes.
     *
     * @param taskService TaskService implementation.
     */
    HttpTaskService_v1( TaskService_v1 taskService )
    {
        super( new JacksonJsonSerializer() );
        this.taskService = taskService;
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
            final String retrieveResponsePayload = getJsonSerializer().encode( response );

            return Response.ok( retrieveResponsePayload, RESOURCE_MEDIA_TYPE ).build();
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

            final String resultsSaveResponsePayload = getJsonSerializer().encode( response );
            return Response.ok( resultsSaveResponsePayload, RESOURCE_MEDIA_TYPE ).build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( new SaveTaskResultResponse( ex ) );
        }
    }

    @POST
    @Path( ADD_TASK_PATH )
    public Response addTask( @Context HttpHeaders headers, final UrlTask task )
    {
        try
        {
            taskService.addTask( task );

            return Response.ok().type( RESOURCE_MEDIA_TYPE ).build();

        } catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    @GET
    @Path( GET_TASKS )
    public Response getTasks( @Context HttpHeaders headers,
                              @QueryParam( GET_TASKS_OFFSET_PARAM ) int offset,
                              @QueryParam( GET_TASKS_LIMIT_PARAM ) int limit )
    {
        try
        {
            final List<UrlTask> tasks = taskService.getTasks( offset, limit );

            final String getTasksResponsePayload = getJsonSerializer()
                    .encode( tasks.toArray( new UrlTask[ tasks.size() ] ) );

            return Response.ok( getTasksResponsePayload, RESOURCE_MEDIA_TYPE ).build();

        } catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    private Response buildExceptionResponse( final ErrorKeeper responseEntity )
    {
        final Exception error = responseEntity.getError();
        logger.error( error.getMessage(), error );

        final String errorEntityJsonPayload = getJsonSerializer().encode( responseEntity );

        return Response.status( getErrorResponseStatus( error ) )
                .type( RESOURCE_MEDIA_TYPE )
                .entity( errorEntityJsonPayload )
                .build();
    }
}