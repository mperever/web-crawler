package com.github.mperever.web.crawler.ts.common.dal;

import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

/**
 * Represents interface for repository with operations for {@link UrlTask} and task results.
 *
 * @author mperever
 */
public interface TaskServiceRepository
{
    /**
     * Adds non-existent/new tasks.
     *
     * @param tasks The tasks to add.
     */
    void addIfNotExist( UrlTask... tasks );

    /**
     * Gets a task by specified url.
     *
     * @param url The url to find task.
     * @return UrlTask object if task is found otherwise returns null.
     */
    UrlTask getTask( String url );

    /**
     * Updates error count of task.
     *
     * @param url The url to find task.
     * @param errorCount The value of error count to set.
     */
    void updateErrorCount( String url, int errorCount );

    /**
     * Saves a task results and
     * sets the value of now time to {@link UrlTask#endProcessTime} for {@link TaskResultEntities#taskId}.
     *
     * @param taskResults The task results to save.
     */
    void saveTaskResults( TaskResultEntities taskResults );

    /**
     * Gets a page text stats of task.
     *
     * @param url The value of url to find task
     * @return The page text stats entity
     */
    TaskPageTextStats getPageTextStats( String url );

    /**
     * Gets a free tasks regarding to specified criteria and assign the tasks to client.
     * Also this method sets the value of now time to {@link UrlTask#startProcessTime} of each task.
     *
     * Search criteria:
     *   - External url depth must be less or equal than specified depth limit
     *   - Processing time is less than specified timeout in milliseconds
     *
     * @param clientId The client id to assign to result tasks
     * @param maxCount The maximum web document objects count
     * @param depthLimit The maximum depth limit for external urls
     * @param timeOutInMs The timeout in milliseconds after which the task is considered free for processing
     * @param errorThreshold The error count threshold after that url will not take for processing.
     * @return The free tasks regarding specified criteria
     */
    UrlTask[] getTasksForClient( String clientId,
                                 int maxCount,
                                 int depthLimit,
                                 long timeOutInMs,
                                 int errorThreshold );
}