package com.github.mperever.dal.mysql.internal;

import javax.persistence.EntityManager;

/**
 * Represents the interface to perform queries in on {@link EntityManager} and return result.
 * Just trying to avoid code duplication.
 *
 * @author mperever
 */
@FunctionalInterface
public interface EntityManagerQueryResult<T>
{
    T execute( EntityManager session );
}