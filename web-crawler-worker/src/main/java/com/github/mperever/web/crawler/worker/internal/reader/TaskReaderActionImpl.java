package com.github.mperever.web.crawler.worker.internal.reader;

import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for {@link TaskReaderAction}.
 *
 * @author mperever
 */
public class TaskReaderActionImpl implements TaskReaderAction
{
    private static final Logger logger = LoggerFactory.getLogger( TaskReaderActionImpl.class );

    private final TaskService_v1 taskService;
    private final String clientId;
    private final int urlDepthLimit;

    public TaskReaderActionImpl( TaskService_v1 taskService, String clientId, int urlDepthLimit )
    {
        this.taskService = taskService;
        this.clientId = clientId;
        this.urlDepthLimit = urlDepthLimit;
    }

    @Override
    public UrlTask[] takeTasks( int maxTaskCount )
    {
        final RetrieveTasksRequest tasksRequest = new RetrieveTasksRequest( clientId, maxTaskCount, urlDepthLimit );
        final RetrieveTasksResponse tasksResponse = taskService.retrieveTasks( tasksRequest );
        if ( tasksResponse.hasError() )
        {
            final Exception error = tasksResponse.getError();
            logger.error( error.getMessage(), error );
            return new UrlTask[0];
        }

        return tasksResponse.getTasks();
    }
}