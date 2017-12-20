package com.github.mperever.web.crawler.auth.rest;

import com.github.mperever.web.crawler.common.auth.Credentials;
import com.github.mperever.web.crawler.common.auth.Role;
import com.github.mperever.web.crawler.common.auth.User;
import com.github.mperever.web.crawler.common.auth.UserInfo;

import java.security.AccessControlException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

/**
 * Represents interface for high level API of authorization service version 1.
 *
 * @author mperever
 */
public interface AuthService_v1
{
    /**
     * Authenticates a user and returns security token.
     *
     * @param credentials The user credentials for authentication
     * @return The security token for feature authorization
     * @throws IllegalArgumentException if parameters are not specified
     * @throws AuthenticationException if user authentication fail
     */
    String authenticate( Credentials credentials ) throws IllegalArgumentException, AuthenticationException;

    /**
     * Creates new user if not exist one.
     * Only users with role {@link Role#ADMIN} eligible to perform this operation.
     *
     * @param securityToken Security token of admin user
     * @param user New user to create
     * @throws IllegalArgumentException if parameters are not specified
     * @throws AuthenticationException if security token is not valid
     * @throws AccessControlException if user does not allow to perform this operation
     */
    void createUser( String securityToken, User user )
            throws IllegalArgumentException, AuthenticationException, AccessControlException;

    /**
     * Returns information about all users.
     * Only users with role {@link Role#ADMIN} eligible to perform this operation.
     *
     * @param securityToken Security token of admin user
     * @return all users
     * @throws IllegalArgumentException if parameter is not specified
     * @throws AuthenticationException if security token is not valid
     * @throws AccessControlException if user does not allow to perform this operation
     */
    List<UserInfo> readUsers( String securityToken )
            throws IllegalArgumentException, AuthenticationException, AccessControlException;

    /**
     * Updates the specified user.
     * Only users with role {@link Role#ADMIN} eligible to perform this operation.
     *
     * @param securityToken Security token of admin user
     * @param user The user to update
     * @throws IllegalArgumentException if parameters are not specified
     * @throws AuthenticationException if security token is not valid
     * @throws AccessControlException if user does not allow to perform this operation
     */
    void updateUser( String securityToken, User user )
            throws IllegalArgumentException,AuthenticationException, AccessControlException;

    /**
     * Deletes user with specified user name.
     * Only users with role {@link Role#ADMIN} eligible to perform this operation.
     *
     * @param securityToken Security token of admin user
     * @param userName The user name to find user and delete
     * @throws IllegalArgumentException if parameters are not specified
     * @throws AuthenticationException if security token is not valid
     * @throws AccessControlException if user does not allow to perform this operation
     */
    void deleteUser( String securityToken, String userName )
            throws IllegalArgumentException, AuthenticationException, AccessControlException;
}