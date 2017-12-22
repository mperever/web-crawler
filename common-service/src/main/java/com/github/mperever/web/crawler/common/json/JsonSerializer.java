package com.github.mperever.web.crawler.common.json;

import java.io.Serializable;

/**
 * Represents facade interface for json serialization.
 * The interface is used by services and clients
 *
 * @author mperever
 */
public interface JsonSerializer
{
    String encode( Serializable object );

    <T> T decode( String json, Class<T> type );
}