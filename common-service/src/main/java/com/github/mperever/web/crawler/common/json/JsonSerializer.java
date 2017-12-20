package com.github.mperever.web.crawler.common.json;

import java.io.Serializable;

/**
 * Represents interface for json serialization.
 * The interface is used by task service clients and server
 *
 * @author mperever
 */
public interface JsonSerializer
{
    String encode( Serializable object );

    <T> T decode( String json, Class<T> type );
}