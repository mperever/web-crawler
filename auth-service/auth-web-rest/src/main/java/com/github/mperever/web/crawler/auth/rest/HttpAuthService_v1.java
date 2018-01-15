package com.github.mperever.web.crawler.auth.rest;

import com.github.mperever.web.crawler.auth.common.AuthService_v1;
import com.github.mperever.web.crawler.auth.common.dto.Credentials;
import com.github.mperever.web.crawler.auth.common.dto.User;
import com.github.mperever.web.crawler.auth.common.dto.UserPrincipal;
import com.github.mperever.web.crawler.auth.dal.mysql.AuthServiceRepositoryMySql;
import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.common.rest.HttpService;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static com.github.mperever.web.crawler.auth.rest.HttpAuthService_v1.SERVICE_ROOT_PATH;

/**
 * Represents a servlet for {@link AuthService_v1}.
 * The servlet uses default implementations: {@link AuthService_v1Impl} and {@link AuthServiceRepositoryMySql}.
 *
 * @author mperever
 */
@Path( SERVICE_ROOT_PATH )
public class HttpAuthService_v1 extends HttpService
{
    static final String SERVICE_ROOT_PATH = "/v1";

    private static final String AUTHENTICATE_PATH = "authenticate";
    private static final String CREATE_NEW_USER_PATH = "createUser";
    private static final String READ_USERS_PATH = "readUsers";
    private static final String UPDATE_USER_PATH = "updateUser";
    private static final String DELETE_USER_PATH = "deleteUser";
    private static final String  USER_NAME_QUERY_PARAM = "userName";

    private final AuthService_v1 authService;

    /**
     * This constructor should be used only for testing purposes.
     *
     * @param authService AuthService implementation.
     */
    HttpAuthService_v1( AuthService_v1 authService )
    {
        super( new JacksonJsonSerializer() );
        this.authService = authService;
    }

    public HttpAuthService_v1()
    {
        this( new AuthService_v1Impl() );
    }

    @POST
    @Path( AUTHENTICATE_PATH )
    @Consumes( RESOURCE_MEDIA_TYPE )
    public Response authenticate( final Credentials credentials )
    {
        try
        {
            final String securityToken = authService.authenticate( credentials );

            return Response.ok().header( SECURITY_TOKEN_HEADER_PARAM, securityToken ).build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    @POST
    @Path( CREATE_NEW_USER_PATH )
    @Consumes( RESOURCE_MEDIA_TYPE )
    public Response createUser( @HeaderParam( SECURITY_TOKEN_HEADER_PARAM ) String securityToken, final User newUser )
    {
        try
        {
            authService.createUser( securityToken, newUser );

            return Response.ok().build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    @GET
    @Path( READ_USERS_PATH )
    @Produces( RESOURCE_MEDIA_TYPE )
    public Response readUsers( @HeaderParam( SECURITY_TOKEN_HEADER_PARAM ) String securityToken )
    {
        try
        {
            final List<UserPrincipal> users = authService.readUsers( securityToken );

            final String readUsersResponsePayload = getJsonSerializer()
                    .encode( users.toArray( new UserPrincipal[ users.size() ] ) );

            return Response.ok( readUsersResponsePayload, RESOURCE_MEDIA_TYPE ).build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    @PUT
    @Path( UPDATE_USER_PATH )
    @Consumes( RESOURCE_MEDIA_TYPE )
    public Response updateUser( @HeaderParam( SECURITY_TOKEN_HEADER_PARAM ) String securityToken, final User user )
    {
        try
        {
            authService.updateUser( securityToken, user );

            return Response.ok().build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }

    @DELETE
    @Path( DELETE_USER_PATH )
    public Response deleteUser( @HeaderParam( SECURITY_TOKEN_HEADER_PARAM ) String securityToken,
                                @QueryParam( USER_NAME_QUERY_PARAM ) final String userName )
    {
        try
        {
            authService.deleteUser( securityToken, userName );

            return Response.ok().build();
        }
        catch ( Exception ex )
        {
            return buildExceptionResponse( ex );
        }
    }
}