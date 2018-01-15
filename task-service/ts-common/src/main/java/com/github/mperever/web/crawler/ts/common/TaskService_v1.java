package com.github.mperever.web.crawler.ts.common;

import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents interface for high level API of task service version 1.
 *
 * @author mperever
 */
public interface TaskService_v1
{
    /**
     * Retrieves tasks for following processing by client.
     *
     * @param request The request from client to retrieve tasks
     * @return Response with tasks for client OR error
     * @throws IllegalArgumentException if request parameters are not valid
     */
    RetrieveTasksResponse retrieveTasks( RetrieveTasksRequest request ) throws IllegalArgumentException;

    /**
     * Saves task results.
     *
     * @param request The request from client to save task results.
     * @return Response with result of task saving.
     * @throws NoSuchElementException if task does not exist
     * @throws IllegalArgumentException if request parameters are not valid
     */
    SaveTaskResultResponse saveTaskResults( SaveTaskResultRequest request )
            throws IllegalArgumentException, NoSuchElementException;

    /**
     * Adds a new task.
     *
     * @param task The task to add
     * @throws IllegalArgumentException if task parameter is null or invalid.
     */
    void addTask( UrlTask task ) throws IllegalArgumentException;

    /**
     * Gets tasks according to specified search criteria.
     *
     * @param offset position of the first result, numbered from 0
     * @param limit  maximum number of tasks to get, greater than 0
     * @return list of tasks
     * @throws IllegalArgumentException if parameters are not valid
     */
    List<UrlTask> getTasks( int offset, int limit ) throws IllegalArgumentException;
}