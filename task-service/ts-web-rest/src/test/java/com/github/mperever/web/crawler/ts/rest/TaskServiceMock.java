package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.common.json.JsonSerializer;

import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskServiceMock implements TaskService_v1
{
    private final JsonSerializer serializer;
    private final Map<String, String> requestResponse = new ConcurrentHashMap<>();
    private final List<UrlTask> tasks = new CopyOnWriteArrayList<>();

    public TaskServiceMock( JsonSerializer serializer )
    {
        this.serializer = serializer;
    }

    public void addResponse( String requestId, Serializable response )
    {
        final String responseJson = serializer.encode( response );
        requestResponse.put( requestId, responseJson );
    }

    @Override
    public RetrieveTasksResponse retrieveTasks( RetrieveTasksRequest request )
    {
        if ( request == null )
        {
            throw new IllegalArgumentException();
        }
        return getResponse( request.getClientId(), RetrieveTasksResponse.class );
    }

    @Override
    public SaveTaskResultResponse saveTaskResults( SaveTaskResultRequest request )
    {
        if ( request == null )
        {
            throw new IllegalArgumentException();
        }
        return getResponse( request.getClientId(), SaveTaskResultResponse.class );
    }

    @Override
    public void addTask( UrlTask task ) throws IllegalArgumentException
    {
        tasks.add( task );
    }

    @Override
    public List<UrlTask> getTasks( int offset, int limit ) throws IllegalArgumentException
    {
        final int toIndex = limit >= tasks.size() ? tasks.size() - 1 : limit;
        return tasks.subList( offset, toIndex );
    }

    private <T> T getResponse( String requestId, Class<T> responseType )
    {
        if ( requestResponse.containsKey( requestId ) )
        {
            final String responseJson = requestResponse.get( requestId );
            return serializer.decode( responseJson, responseType );
        }

        throw new RuntimeException( "There is no response for request:" + requestId );
    }
}