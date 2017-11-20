package com.github.mperever.dal.mysql.internal;

import org.hibernate.Session;

/**
 * Represents the interface to perform queries in on {@link Session} and return result.
 * Just trying to avoid code duplication.
 *
 * @author mperever
 */
@FunctionalInterface
public interface SessionQueryResult<T>
{
    T execute( Session session );
}