package com.github.mperever.web.crawler.worker.internal.reader;

import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

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