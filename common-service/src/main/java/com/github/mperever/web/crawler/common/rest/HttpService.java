package com.github.mperever.web.crawler.common.rest;

import com.github.mperever.web.crawler.common.json.JsonSerializer;

import java.security.AccessControlException;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents base class for service servlet.
 *
 * @author mperever
 */
public abstract class HttpService
{
    private static final Logger logger = LoggerFactory.getLogger( HttpService.class );

    protected static final String SECURITY_TOKEN_HEADER_PARAM = "security-token";
    public static final String RESOURCE_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final JsonSerializer jsonSerializer;

    public HttpService( JsonSerializer jsonSerializer )
    {
        this.jsonSerializer = jsonSerializer;
    }

    protected JsonSerializer getJsonSerializer()
    {
        return this.jsonSerializer;
    }

    protected Response buildExceptionResponse( final Exception error )
    {
        logger.error( error.getMessage(), error );

        final String jsonResponse = jsonSerializer.encode( error );

        return Response.status( getErrorResponseStatus( error ) )
                .type( RESOURCE_MEDIA_TYPE )
                .entity( jsonResponse )
                .build();
    }

    protected Response.Status getErrorResponseStatus( final Exception error )
    {
        final Class errorType = error.getClass();
        if ( errorType.equals( IllegalArgumentException.class ) )
        {
            return Response.Status.BAD_REQUEST;
        }

        if ( errorType.equals( AuthenticationException.class ) )
        {
            return Response.Status.UNAUTHORIZED;
        }

        if ( errorType.equals( AccessControlException.class ) )
        {
            return Response.Status.FORBIDDEN;
        }

        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}