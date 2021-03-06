package com.github.mperever.web.crawler.auth.dal;

import com.github.mperever.web.crawler.auth.common.dto.User;

import java.util.List;

/**
 * Represents interface for repository with operations for {@link User}.
 *
 * @author mperever
 */
public interface AuthServiceRepository
{
    /**
     * Adds new user.
     *
     * @param newUser The user to add
     */
    void addUserIfNotExist( User newUser );

    /**
     * Gets a user by specified name.
     *
     * @param userName The user name to find
     * @return user object or null if user does not exist
     */
    User getUserByName( String userName );

    /**
     * Gets added users.
     *
     * @param offset position of the first result, numbered from 0
     * @param limit  maximum number of users to get, greater than 0
     * @return list of users
     */
    List<User> getUsers( int offset, int limit );

    /**
     * Updates user data.
     *
     * @param user The user to update
     */
    void updateUser( User user );

    /**
     * Deletes existed user.
     *
     * @param userName The user name to find
     */
    void deleteUser( String userName );
}