package com.github.mperever.web.crawler.worker.internal.reader;

import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

/**
 * Represents interface for thread-safe task reader.
 *
 * @author mperever
 */
public interface BufferedTaskReader extends AutoCloseable
{
    /**
     * Gets the maximum buffer capacity.
     *
     * @return The maximum buffer capacity.
     */
    int getMaxCapacity();

    /**
     * Reads task from source.
     * This method blocks thread until task appears.
     *
     * @return UrlTask object from source.
     */
    UrlTask read();

    /**
     * Closes reader resources
     */
    void close();
}