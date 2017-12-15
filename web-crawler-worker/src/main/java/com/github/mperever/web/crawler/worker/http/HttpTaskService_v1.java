package com.github.mperever.web.crawler.worker.http;

import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.ts.common.json.JsonSerializer;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for {@link TaskService_v1}.
 * This implementation sends http requests to task service using {@link HttpClient}.
 */
public class HttpTaskService_v1 implements TaskService_v1, AutoCloseable
{
    private static final Logger logger = LoggerFactory.getLogger( HttpTaskService_v1.class );

    private static final String RETRIEVE_TASKS_PATH = "/tasks.retrieve";
    private static final String SAVE_TASK_RESULTS_PATH = "/results.save";
    private static final String CONTENT_TYPE = "application/json";
    private static final int OK = 200;

    private final JsonSerializer serializer = new JacksonJsonSerializer();
    private final HttpClient httpClient = new HttpClient();

    private final String retrieveTasksUrl;
    private final String saveTaskResultsUrl;

    private boolean isStarted;

    public HttpTaskService_v1( String taskServiceUrl )
    {
        httpClient.setFollowRedirects( false );
        retrieveTasksUrl = taskServiceUrl + RETRIEVE_TASKS_PATH;
        saveTaskResultsUrl = taskServiceUrl + SAVE_TASK_RESULTS_PATH;
    }

    @Override
    public RetrieveTasksResponse retrieveTasks( final RetrieveTasksRequest request )
    {
        try
        {
            return sendPostRequest( request, retrieveTasksUrl, RetrieveTasksResponse.class );

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            return new RetrieveTasksResponse( ex );
        }
    }

    @Override
    public SaveTaskResultResponse saveTaskResults( final SaveTaskResultRequest request )
    {
        try
        {
            return sendPostRequest( request, saveTaskResultsUrl, SaveTaskResultResponse.class );

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            return new SaveTaskResultResponse( ex );
        }
    }

    private HttpClient getHttpClient()
    {
        if ( !isStarted )
        {
            try
            {
                httpClient.start();
                isStarted = true;

            } catch ( Exception ex )
            {
                logger.error( ex.getMessage(), ex );
            }
        }
        return httpClient;
    }

    private <T> T sendPostRequest( final Serializable request, String url, Class<T> responseType ) throws Exception
    {
        final String requestBody = serializer.encode( request );
        final ContentResponse response = getHttpClient().POST( url )
                .content( new StringContentProvider( CONTENT_TYPE, requestBody, StandardCharsets.UTF_8 ) )
                .send();
        return getResponse( response, responseType );
    }

    private <T> T getResponse( final ContentResponse response, Class<T> type )
    {
        final boolean isSuccess = response.getStatus() == OK;
        final String responseContent = response.getContentAsString();
        if ( !isSuccess )
        {
            throw  new IllegalStateException( "An error occurs during sending a request. Error: " + responseContent );
        }

        return serializer.decode( responseContent, type );
    }

    @Override
    public void close()
    {
        if ( isStarted )
        {
            try
            {
                httpClient.stop();

            } catch ( Exception ex )
            {
                logger.error( ex.getMessage(), ex );
            }
        }
    }
}