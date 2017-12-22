package com.github.mperever.web.crawler.auth.rest;

import com.github.mperever.web.crawler.auth.common.AuthService_v1;
import com.github.mperever.web.crawler.auth.common.Jwt;
import com.github.mperever.web.crawler.auth.dal.AuthServiceRepository;
import com.github.mperever.web.crawler.auth.common.dto.Credentials;
import com.github.mperever.web.crawler.auth.common.dto.Role;
import com.github.mperever.web.crawler.auth.common.dto.User;
import com.github.mperever.web.crawler.auth.common.dto.UserInfo;
import com.github.mperever.web.crawler.auth.common.utils.DigestUtils;
import com.github.mperever.web.crawler.auth.dal.mysql.AuthServiceRepositoryMySql;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.AccessControlException;
import java.security.NoSuchAlgorithmException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;

/**
 * Represents implementation for {@link AuthService_v1}.
 *
 * @author mperever
 */
public class AuthService_v1Impl implements AuthService_v1
{
    private static final RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

    private final AuthServiceRepository repository;
    private final Jwt jwt;

    /**
     * This constructor should be used only for testing purposes.
     *
     * @param jwt Java Web Token builder and creator.
     */
    AuthService_v1Impl( AuthServiceRepository repository, Jwt jwt )
    {
        this.jwt = jwt;
        this.repository = repository;
    }

    public AuthService_v1Impl()
    {
        jwt = new Jwt();
        repository = new AuthServiceRepositoryMySql();
    }

    @Override
    public String authenticate( final Credentials credentials )
            throws IllegalArgumentException, AuthenticationException
    {
        checkCredentialsNotEmpty( credentials );

        final UserInfo userInfo = authenticateUser( credentials );

        final String issuer = mxBean.getName();
        final Date expirationDate = calcJwtExpirationDate();
        return jwt.create( userInfo, issuer, expirationDate );
    }

    private static void checkCredentialsNotEmpty( final Credentials credentials )
            throws IllegalArgumentException
    {
        if ( credentials == null )
        {
            throw new IllegalArgumentException( "The parameter 'credentials' is null" );
        }
        final String userName = credentials.getUserName();
        if ( userName == null || userName.isEmpty() )
        {
            throw new IllegalArgumentException( "User name is not specified" );
        }
        final String password = credentials.getPassword();
        if ( password == null || password.isEmpty() )
        {
            throw new IllegalArgumentException( "Password is not specified" );
        }
    }

    private UserInfo authenticateUser( final Credentials credentials ) throws AuthenticationException
    {
        final String userName = credentials.getUserName();
        final User user = repository.getUserByName( userName );

        final boolean isUserExist = user != null;
        if ( !isUserExist )
        {
            throw new AuthenticationException( String.format( "User '%s' is not setup in authorisation service", userName ) );
        }

        try
        {
            if ( !isCredentialsValid( credentials, user ) )
            {
                throw new AuthenticationException( "The credentials are not valid" );
            }

        } catch ( NoSuchAlgorithmException ex )
        {
            throw new AuthenticationException( "An internal error occurred during credentials validation", ex );
        }

        return new UserInfo( user.getName(), user.getRole() );
    }

    private static boolean isCredentialsValid( final Credentials credentials, User user ) throws NoSuchAlgorithmException
    {
        final String userName = credentials.getUserName();
        final String password = credentials.getPassword();
        final String actualHash = DigestUtils.hash( userName, password );

        final String expectedHash = user.getPassword();
        return expectedHash.equals( actualHash );
    }

    private static Date calcJwtExpirationDate()
    {
        final Date today = new Date();
        final Date tomorrow = new Date( today.getTime() + TimeUnit.DAYS.toMillis( 1 ) );

        return tomorrow;
    }

    // TODO: Check input parameters (IllegalArgumentException)

    @Override
    public void createUser( String securityToken, User user )
            throws IllegalArgumentException, AuthenticationException, AccessControlException
    {
        checkAdminRole( securityToken );

        final User userToSave = hashUserPassword( user );

        repository.addUserIfNotExist( userToSave );
    }

    @Override
    public List<UserInfo> readUsers( String securityToken )
            throws IllegalArgumentException, AuthenticationException, AccessControlException
    {
        checkAdminRole( securityToken );

        return repository.getUsers( Integer.MAX_VALUE ).stream()
                .map( user -> new UserInfo( user.getName(), user.getRole() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public void updateUser( String securityToken, User user )
            throws IllegalArgumentException, AuthenticationException, AccessControlException
    {
        checkAdminRole( securityToken );

        final String password = user.getPassword();
        final boolean isPasswordSet = password != null && !password.isEmpty();
        final User userToUpdate = isPasswordSet ? hashUserPassword( user ) : user;

        repository.updateUser( userToUpdate );
    }

    @Override
    public void deleteUser( String securityToken, String userName )
            throws IllegalArgumentException, AuthenticationException, AccessControlException
    {
        checkAdminRole( securityToken );

        repository.deleteUser( userName );
    }

    private void checkAdminRole( String token ) throws AuthenticationException, AccessControlException
    {
        final UserInfo userInfo = jwt.parseUserInfo( token );
        if ( userInfo == null )
        {
            throw new AuthenticationException( "Token is not valid" );
        }

        if ( userInfo.getRole() != Role.ADMIN )
        {
            throw new AccessControlException( String.format( "User '%s' does not have permissions to perform this operation", userInfo.getName() ) );
        }
    }

    private static User hashUserPassword( User user )
    {
        try
        {
            final String passwordHash = DigestUtils.hash( user.getName(), user.getPassword() );

            return new User( user.getName(), passwordHash, user.getRole() );

        } catch ( NoSuchAlgorithmException ex )
        {
            throw new IllegalStateException( "An internal error occurred during creating password hash", ex );
        }
    }
}