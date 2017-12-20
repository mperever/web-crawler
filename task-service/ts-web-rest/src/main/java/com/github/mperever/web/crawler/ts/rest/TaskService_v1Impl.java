package com.github.mperever.web.crawler.ts.rest;

import com.github.mperever.web.crawler.ts.common.TaskService_v1;
import com.github.mperever.web.crawler.ts.common.dal.TaskPageTextStats;
import com.github.mperever.web.crawler.ts.common.dal.TaskResultEntities;
import com.github.mperever.web.crawler.ts.common.dal.TaskServiceRepository;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.TaskResults;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;
import com.github.mperever.web.crawler.ts.rest.internal.ArgumentsValidator;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for {@link TaskService_v1}.
 *
 * @author mperever
 */
public class TaskService_v1Impl implements TaskService_v1
{
    private static final Logger logger = LoggerFactory.getLogger( TaskService_v1Impl.class );

    private final static long DEFAULT_TASK_PROCESSING_TIMEOUT = TimeUnit.SECONDS.toMillis( 60 );
    private final static int DEFAULT_TASK_ERROR_THRESHOLD = 5;
    private final static String REQUEST_BODY_MISSING_TEMPLATE = "Could not perform '%s', request body is not specified";

    private final TaskServiceRepository repository;
    private final long taskProcessingTimeoutMs;
    private final int taskErrorThreshold;

    public TaskService_v1Impl( TaskServiceRepository repository )
    {
        this.repository = repository;
        this.taskProcessingTimeoutMs = DEFAULT_TASK_PROCESSING_TIMEOUT;
        this.taskErrorThreshold = DEFAULT_TASK_ERROR_THRESHOLD;
    }

    public TaskService_v1Impl( TaskServiceRepository repository,
                               long taskProcessingTimeoutMs,
                               int taskErrorThreshold )
    {
        this.repository = repository;
        this.taskProcessingTimeoutMs = taskProcessingTimeoutMs;
        this.taskErrorThreshold = taskErrorThreshold;
    }

    @Override
    public RetrieveTasksResponse retrieveTasks( final RetrieveTasksRequest request )
    {
        final String errorMessage = validateRetrieveRequest( request );
        if ( !errorMessage.isEmpty() )
        {
            throw new IllegalArgumentException( errorMessage );
        }

        try
        {
            final UrlTask[] tasks = repository.getTasksForClient(
                    request.getClientId(),
                    request.getMaxCount(),
                    request.getDepthLimit(),
                    taskProcessingTimeoutMs,
                    taskErrorThreshold );

            return new RetrieveTasksResponse( tasks );

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            return new RetrieveTasksResponse( ex );
        }
    }

    private String validateRetrieveRequest( final RetrieveTasksRequest request )
    {
        if ( request == null )
        {
            return String.format( REQUEST_BODY_MISSING_TEMPLATE, "retrieve tasks" );
        }

        final ArgumentsValidator requestValidator = new ArgumentsValidator()
                .notEmpty( request.getClientId(), "clientId" )
                .numberPositive( request.getMaxCount(), "maxCount" )
                .numberNotNegative( request.getDepthLimit(), "depthLimit" );

        return validationResultsToString( requestValidator.validate() );
    }

    @Override
    public SaveTaskResultResponse saveTaskResults( final SaveTaskResultRequest request )
    {
        final String errorMessage = validateSaveResultRequest( request );
        if ( !errorMessage.isEmpty() )
        {
            throw new IllegalArgumentException( errorMessage );
        }

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

    private String validateSaveResultRequest( final SaveTaskResultRequest request )
    {
        if ( request == null )
        {
            return String.format( REQUEST_BODY_MISSING_TEMPLATE, "save task results" );
        }

        final ArgumentsValidator requestValidator = new ArgumentsValidator()
                .notEmpty( request.getClientId(), "clientId" )
                .notEmpty( request.getUrl(), "url" );

        if ( request.getError() == null )
        {
            requestValidator.notNull( request.getTaskResults(), "taskResults" );
        }

        return validationResultsToString( requestValidator.validate() );
    }

    private static String validationResultsToString( String[] errors )
    {
        return String.join( ", ", errors );
    }

    private void saveTaskResults( final UrlTask task, final TaskResults results )
    {
        final UrlTask[] newTasks = createTasks( task, results.getNewUrls() );

        // Add new tasks to save
        final TaskResultEntities resultsToSave = new TaskResultEntities( task.getId() );
        resultsToSave.setTasks( newTasks );

        // Add page text and words stats to save
        final String pageText = results.getPageText();
        final Map<String, Long> wordsStats = results.getWordsStats();
        final boolean isPageTextPresent = pageText != null && !pageText.isEmpty();
        final boolean isWordStatsPresent = wordsStats != null && !wordsStats.isEmpty();
        final boolean isSavePageTextStats = isPageTextPresent || isWordStatsPresent;

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