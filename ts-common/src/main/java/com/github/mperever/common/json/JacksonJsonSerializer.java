package com.github.mperever.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents Jackson implementation for {@link JsonSerializer}.
 *
 * @author mperever
 */
public class JacksonJsonSerializer implements JsonSerializer
{
    private static final Logger logger = LoggerFactory.getLogger( JacksonJsonSerializer.class );

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer();

    @Override
    public String encode( final Serializable object )
    {
        try
        {
            return writer.writeValueAsString( object );

        } catch ( JsonProcessingException ex )
        {
            logger.error( ex.getMessage() );
            return String.format( "{\"error\":\"Could not encode object to json. object: '%s'\"}", object );
        }
    }

    @Override
    public <T> T decode( String json, Class<T> type )
    {
        final ObjectReader reader = mapper.readerFor( type );
        try
        {
            return ( T ) reader.readValue( json );

        } catch ( IOException ex )
        {
            logger.error( ex.getMessage() );
            throw new IllegalArgumentException( ex );
        }
    }
}