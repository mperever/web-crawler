package com.github.mperever.rest;

import com.github.mperever.common.TaskService_v1;
import com.github.mperever.common.dal.TaskPageTextStats;
import com.github.mperever.common.dal.TaskResultEntities;
import com.github.mperever.common.dal.TaskServiceRepository;
import com.github.mperever.common.dto.RetrieveTasksRequest;
import com.github.mperever.common.dto.RetrieveTasksResponse;
import com.github.mperever.common.dto.SaveTaskResultRequest;
import com.github.mperever.common.dto.SaveTaskResultResponse;
import com.github.mperever.common.dto.TaskResults;
import com.github.mperever.common.dto.UrlTask;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for {@link TaskService_v1}.
 */
public class TaskService_v1Impl implements TaskService_v1
{
    private static final Logger logger = LoggerFactory.getLogger( TaskService_v1Impl.class );

    private final static long DEFAULT_TASK_STAGE_TIMEOUT = TimeUnit.SECONDS.toMillis( 60 );
    private final static int DEFAULT_TASK_ERROR_THRESHOLD = 5;

    private final TaskServiceRepository repository;
    private final long taskStageTimeoutMs;
    private final int taskErrorThreshold;

    public TaskService_v1Impl( TaskServiceRepository repository )
    {
        this.repository = repository;
        this.taskStageTimeoutMs = DEFAULT_TASK_STAGE_TIMEOUT;
        this.taskErrorThreshold = DEFAULT_TASK_ERROR_THRESHOLD;
    }

    public TaskService_v1Impl( TaskServiceRepository repository,
                               long taskStageTimeoutMs,
                               int taskErrorThreshold )
    {
        this.repository = repository;
        this.taskStageTimeoutMs = taskStageTimeoutMs;
        this.taskErrorThreshold = taskErrorThreshold;
    }

    @Override
    public RetrieveTasksResponse retrieveTasks( final RetrieveTasksRequest request )
    {
        try
        {
            final UrlTask[] tasks = repository.getTasksForClient(
                    request.getClientId(),
                    request.getMaxCount(),
                    request.getDepthLimit(),
                    taskStageTimeoutMs,
                    taskErrorThreshold );

            return new RetrieveTasksResponse( tasks );

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            return new RetrieveTasksResponse( ex );
        }
    }

    @Override
    public SaveTaskResultResponse saveTaskResults( final SaveTaskResultRequest request )
    {
        final String url = request.getUrl();

        try
        {
            final UrlTask task = repository.getTask( url );
            if ( task == null )
            {
                throw new NullPointerException( "Could not find task by url: " + url );
            }

            final SaveTaskResultResponse successResponse = new SaveTaskResultResponse();

            boolean isTaskAssignToClient = request.getClientId().equals( task.getClientId() );
            if ( !isTaskAssignToClient )
            {
                logger.warn(
                        "Task results from client '{}' are not saved. "
                        + "The task {} was re-assign to another client", request.getClientId(), task );
                return successResponse;
            }

            if ( request.hasError() )
            {
                logger.debug(
                        "Task results are not saved. "
                        + "The client '{}' returned error: {}", request.getClientId(), request.getError() );
                repository.updateErrorCount( task.getUrl(), task.getErrorCount() + 1 );

                return successResponse;
            }

            final TaskResults results = request.getTaskResults();
            this.saveTaskResults( task, results );

            return successResponse;

        } catch ( Exception ex )
        {
            logger.error(  ex.getMessage(), ex );
            return new SaveTaskResultResponse( ex );
        }
    }

    private void saveTaskResults( final UrlTask task, final TaskResults results ) throws Exception
    {
        final UrlTask[] newTasks = createTasks( task, results.getNewUrls() );

        // Add new tasks to save
        final TaskResultEntities resultsToSave = new TaskResultEntities( task.getId() );
        resultsToSave.setTasks( newTasks );

        // Add page text and words stats to save
        final String pageText = results.getPageText();
        final Map<String, Long> wordsStats = results.getWordsStats();
        final boolean isSavePageTextStats = (
                ( pageText != null && !pageText.isEmpty() )
                || ( wordsStats != null && !wordsStats.isEmpty() ) );
        if ( isSavePageTextStats )
        {
            final TaskPageTextStats pageTextStats = new TaskPageTextStats( task.getId() );
            pageTextStats.setPageText( pageText );
            pageTextStats.setWordStats( wordsStats );

            resultsToSave.setStats( pageTextStats );
        }

        repository.saveTaskResults( resultsToSave );
    }

    private UrlTask[] createTasks( final UrlTask task, final String[] urls )
    {
        if ( urls == null || urls.length == 0 )
        {
            return new UrlTask[0];
        }

        final List<UrlTask> tasks = new ArrayList<>( urls.length );
        for ( String url : urls )
        {
            try
            {
                tasks.add( new UrlTask( task, url ) );

            } catch ( URISyntaxException ex )
            {
                logger.error( "Could not convert url '{}' to URI object. Message: {}", url, ex.getMessage() );
            }
        }

        return tasks.toArray( new UrlTask[tasks.size()] );
    }
}