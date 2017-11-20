package com.github.mperever.worker.internal.reader;

import com.github.mperever.common.dto.UrlTask;

/**
 * Represents action for {@link BufferedTaskReaderImpl}.
 *
 * @author mperever
 */
@FunctionalInterface
public interface TaskReaderAction
{
    UrlTask[] takeTasks( int maxTaskCount );
}