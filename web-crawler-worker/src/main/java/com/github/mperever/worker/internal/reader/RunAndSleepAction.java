package com.github.mperever.worker.internal.reader;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents class to run the {@link #action}, and then falls asleep until it is awakened.
 *
 * @author mperever
 */
class RunAndSleepAction implements AutoCloseable
{
    private static final Logger logger = LoggerFactory.getLogger( RunAndSleepAction.class );

    private volatile boolean isWaiting;
    private final Runnable action;
    private final Thread actionRunner = new Thread( this::runActionInLoop );

    public RunAndSleepAction( Runnable action )
    {
        this.action = action;
    }

    private void runActionInLoop()
    {
        logger.info( "Thread is started" );

        while ( !Thread.currentThread().isInterrupted() )
        {
            action.run();

            waitForWakeUp();
        }

        logger.info( "Thread has completed it work" );
    }

    @Override
    public void close()
    {
        if ( !actionRunner.isInterrupted() )
        {
            actionRunner.interrupt();
        }
    }

    public void runAndWait()
    {
        if ( actionRunner.isAlive() )
        {
            wakeUp();
        }
        else
        {
            actionRunner.start();
        }
    }

    public void wakeUp()
    {
        if ( isWaiting )
        {
            synchronized ( this )
            {
                if ( isWaiting )
                {
                    this.notifyAll();
                    isWaiting = false;
                    logger.info( "Thread has woken up!" );
                }
            }
        }
    }

    @SuppressFBWarnings( "WA_NOT_IN_LOOP" )
    private void waitForWakeUp()
    {
        if ( isWaiting )
        {
            return;
        }

        synchronized ( this )
        {
            logger.info( "Thread has fallen to sleep" );

            // while loop to guard against spurious wakeup
            isWaiting = true;
            while ( isWaiting )
            {
                try
                {
                    this.wait();
                } catch ( InterruptedException ex )
                {
                    isWaiting = false;
                    logger.debug( "Thread was interrupted during sleeping/waiting" );
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}