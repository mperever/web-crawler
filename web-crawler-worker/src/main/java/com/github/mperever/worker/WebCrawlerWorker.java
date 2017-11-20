package com.github.mperever.worker;

import com.github.mperever.common.dto.SaveTaskResultRequest;
import com.github.mperever.common.dto.TaskResults;
import com.github.mperever.common.dto.UrlTask;

import com.github.mperever.worker.http.HttpTaskService_v1;
import com.github.mperever.worker.internal.reader.BufferedTaskReader;
import com.github.mperever.worker.internal.reader.BufferedTaskReaderImpl;
import com.github.mperever.worker.internal.reader.TaskReaderActionImpl;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents entry point to run web crawler worker.
 *
 * @author mperever
 */
public class WebCrawlerWorker
{
    private static final Logger logger = LoggerFactory.getLogger( WebCrawlerWorker.class );

    private static final int TASK_EXECUTOR_SHUTDOWN_TIMEOUT_SEC = 15;

    private final UrlTaskProcessor taskProcessor = new UrlTaskProcessor();
    private final String clientId = ManagementFactory.getRuntimeMXBean().getName();
    private final HttpTaskService_v1 taskService;
    private final BufferedTaskReader taskReader;
    private final ExecutorService taskProcessorExecutor;

    WebCrawlerWorker( WorkerParameters parameters )
    {
        taskService = new HttpTaskService_v1( parameters.taskServiceUrl );
        final TaskReaderActionImpl readerAction = new TaskReaderActionImpl(
                taskService,
                clientId,
                parameters.urlDepthLimit );
        taskReader = new BufferedTaskReaderImpl( readerAction, parameters.maxTaskCount );
        taskProcessorExecutor = Executors.newFixedThreadPool( parameters.taskProcessorsNumber );
    }

    /**
     * Run the web crawler worker with specified number of task processors.
     *
     * @param args The parameters for running web crawler worker.
     *             There is a list of required parameters:
     *                  0 - taskServiceUrl - task service url
     *             There is list of optional parameters that can be specified:
     *                  1 - urlDepthLimit - the maximum depth for external urls.
     *                  2 - taskProcessorsNumber - the number of threads that will process tasks.
     *             If optional parameters are not specified then default one will be used.
     */
    public static void main( final String... args )
    {
        final WorkerParameters parameters = WorkerParameters.fromStringArgs( args );
        new WebCrawlerWorker( parameters ).start();
    }

    private void start()
    {
        // Adds virtual-machine gracefully shutdown hook to finalize task processing and other stuff.
        Runtime.getRuntime().addShutdownHook( new Thread( this::onShutdown ) );

        while ( !Thread.currentThread().isInterrupted() )
        {
            try
            {
                final UrlTask task = taskReader.read();
                taskProcessorExecutor.execute( () ->
                {
                    final String taskUrl = task.getUrl();
                    SaveTaskResultRequest taskResultRequest;
                    try
                    {
                        final TaskResults results = taskProcessor.process( taskUrl );
                        taskResultRequest = new SaveTaskResultRequest( clientId, taskUrl, results );
                    } catch ( Exception ex )
                    {
                        taskResultRequest = new SaveTaskResultRequest( clientId, taskUrl, ex );
                    }
                    taskService.saveTaskResults( taskResultRequest );
                } );
            }
            catch ( Exception ex )
            {
                // Processing is not interrupted if an exception occurs.
                logger.error( ex.getMessage(), ex );
            }
        }

        taskService.close();
        logger.debug( "Task processing is finished." );
    }

    private void onShutdown()
    {
        logger.info( "Worker shutdown is started" );

        logger.debug( "Shutdown task executor..." );
        taskProcessorExecutor.shutdown();
        try
        {
            taskProcessorExecutor.awaitTermination( TASK_EXECUTOR_SHUTDOWN_TIMEOUT_SEC, TimeUnit.SECONDS );
        } catch ( InterruptedException ex )
        {
            logger.error( ex.getMessage(), ex );
        }

        logger.debug( "Close task service..." );
        taskService.close();

        logger.info( "Instance shutdown is finished" );
    }
}