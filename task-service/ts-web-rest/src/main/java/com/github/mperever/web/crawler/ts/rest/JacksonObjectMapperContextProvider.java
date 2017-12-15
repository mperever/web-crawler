package com.github.mperever.web.crawler.ts.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.mperever.web.crawler.ts.common.dto.ErrorKeeper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Represents JAX-RS context resolver to overcome issue with polymorphic deserialization of {@link Exception} field
 * in {@link ErrorKeeper}
 *
 * @author mperever
 */
@Provider
@Produces( MediaType.APPLICATION_JSON )
public class JacksonObjectMapperContextProvider implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper defaultMapper = new ObjectMapper();
    private final ObjectMapper errorKeeperMapper = new ObjectMapper();

    public JacksonObjectMapperContextProvider()
    {
        errorKeeperMapper.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
    }

    @Override
    public ObjectMapper getContext( Class<?> type )
    {
        return ErrorKeeper.class.isAssignableFrom( type ) ? errorKeeperMapper : defaultMapper;
    }
}