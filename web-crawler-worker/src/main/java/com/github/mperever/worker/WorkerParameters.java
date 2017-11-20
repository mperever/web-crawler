package com.github.mperever.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents input parameters for web crawler worker.
 *
 * @author mperever
 */
class WorkerParameters
{
    private static final Logger logger = LoggerFactory.getLogger( WorkerParameters.class );

    String taskServiceUrl;

    // Default values
    int urlDepthLimit = 20;
    int taskProcessorsNumber = Runtime.getRuntime().availableProcessors();
    int maxTaskCount = taskProcessorsNumber * 10;

    private WorkerParameters()
    {
    }

    static WorkerParameters fromStringArgs( String[] args )
    {
        final WorkerParameters parameters = new WorkerParameters();

        // 0 - taskServiceUrl
        if ( args == null || args.length == 0 )
        {
            throw new IllegalArgumentException( "The parameter 'taskServiceUrl' is not specified" );
        }
        parameters.taskServiceUrl = args[0];
        logger.debug( "Task service url: " + parameters.taskServiceUrl );

        // 1 - urlDepthLimit
        if ( args.length > 1 )
        {
            final int depthLimit = Integer.parseInt( args[1] );
            parameters.urlDepthLimit = depthLimit < 0 ? 0 : depthLimit;
        }
        logger.debug( "External url depth limit: " + parameters.urlDepthLimit );

        // 2- taskProcessorsNumber
        if ( args.length > 2 )
        {
            final int taskProcessorsNumber = Integer.parseInt( args[2] );
            if ( taskProcessorsNumber > 0 )
            {
                parameters.taskProcessorsNumber = taskProcessorsNumber;
            }
        }
        logger.debug( "Task processors number: " + parameters.taskProcessorsNumber );

        return parameters;
    }
}