package com.github.mperever.web.crawler.worker.internal.reader;

import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for thread-safe task reader.
 * This implementation uses {@link RunAndSleepAction} for taking tasks in separate thread.
 *
 * @author mperever
 */
public class BufferedTaskReaderImpl implements BufferedTaskReader
{
    private static final Logger logger = LoggerFactory.getLogger( BufferedTaskReaderImpl.class );
    private static final int DEFAULT_SLEEP_INTERVAL_SEC = 15;

    private final RunAndSleepAction taskTakingAction = new RunAndSleepAction( this::takeTasksAndPutToBuffer );

    private final TaskReaderAction action;
    private final int maxCapacity;
    private final int sleepTimeInterval;
    private final TimeUnit sleepTimeUnit;
    private final BlockingQueue<UrlTask> buffer;

    private volatile boolean isTaskTaking;

    public BufferedTaskReaderImpl( TaskReaderAction action, int maxCapacity )
    {
        this( action, maxCapacity, DEFAULT_SLEEP_INTERVAL_SEC, TimeUnit.SECONDS );
    }

    public BufferedTaskReaderImpl( TaskReaderAction action,
                                   int maxCapacity,
                                   int sleepTimeInterval,
                                   TimeUnit sleepTimeUnit )
    {
        this.action = action;
        this.maxCapacity = maxCapacity;
        this.sleepTimeInterval = sleepTimeInterval;
        this.sleepTimeUnit = sleepTimeUnit;
        buffer = new ArrayBlockingQueue<>( maxCapacity );
    }

    private void runTaskTakingThread()
    {
        isTaskTaking = true;

        taskTakingAction.runAndWait();
    }

    private void takeTasksAndPutToBuffer()
    {
        boolean isTasksTakenSuccess;
        do
        {
            final int maxTaskCount = getMaxCapacity() - buffer.size();
            if ( maxTaskCount <= 0 )
            {
                logger.warn( "Tasks are not taken due to the value of maximum data count '{}' <= 0. buffer size '{}'",
                        maxTaskCount, buffer.size() );
                break;
            }

            final UrlTask[] tasks = this.action.takeTasks( maxTaskCount );
            isTasksTakenSuccess = tasks != null && tasks.length != 0;

            if ( isTasksTakenSuccess )
            {
                putTasksToBuffer( tasks );
            }
            else
            {
                final boolean isInterrupted = sleep();
                if ( isInterrupted )
                {
                    break;
                }
            }

        } while ( !isTasksTakenSuccess || isBufferNeedFillUp() );

        this.isTaskTaking = false;
    }

    @Override
    public void close()
    {
        taskTakingAction.close();
        buffer.clear();
    }

    @Override
    public int getMaxCapacity()
    {
        return maxCapacity;
    }

    @Override
    public UrlTask read()
    {
        if ( isTaskNeedTaken() )
        {
            synchronized ( this )
            {
                if ( isTaskNeedTaken() )
                {
                    runTaskTakingThread();
                }
            }
        }

        return takeTaskFromBuffer();
    }

    private boolean isTaskNeedTaken()
    {
        return !isTaskTaking && isBufferNeedFillUp();
    }

    private boolean isBufferNeedFillUp()
    {
        return buffer.size() <= getMaxCapacity() / 2;
    }

    private UrlTask takeTaskFromBuffer()
    {
        try
        {
            logger.debug( "Taking task from buffer. buffer size: " + buffer.size() );
            return buffer.take();

        } catch ( InterruptedException ex )
        {
            logger.debug( "Thread was interrupted during taking task from buffer." );
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private void putTasksToBuffer( final UrlTask[] tasks )
    {
        for ( UrlTask task : tasks )
        {
            try
            {
                logger.debug( "Adding task into buffer. buffer size: '{}, task: {}", buffer.size(), task );
                buffer.put( task );

                logger.info( "The task has been added into buffer. " + task );
                logger.debug( "The buffer size after adding: " + buffer.size() );

            } catch ( InterruptedException ex )
            {
                logger.debug( "Thread was interrupted during putting task into buffer" );
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean sleep()
    {
        boolean isInterrupted = false;
        try
        {
            logger.debug( "Sleep for '{} {}' before to repeat actions...", sleepTimeInterval, sleepTimeUnit );

            sleepTimeUnit.sleep( sleepTimeInterval );

        } catch ( InterruptedException ex )
        {
            logger.debug( "Thread was interrupted during sleeping" );
            Thread.currentThread().interrupt();
            isInterrupted = true;
        }

        return isInterrupted;
    }
}