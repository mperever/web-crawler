package com.github.mperever.web.crawler.ts.common;

import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultRequest;
import com.github.mperever.web.crawler.ts.common.dto.SaveTaskResultResponse;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksRequest;
import com.github.mperever.web.crawler.ts.common.dto.RetrieveTasksResponse;

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
     * @throws IllegalArgumentException if request parameters are not valid
     */
    SaveTaskResultResponse saveTaskResults( SaveTaskResultRequest request ) throws IllegalArgumentException;
}