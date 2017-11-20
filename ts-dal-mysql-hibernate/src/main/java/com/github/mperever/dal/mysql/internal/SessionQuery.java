package com.github.mperever.dal.mysql.internal;

import org.hibernate.Session;

/**
 * Represents the interface to perform queries in on {@link Session}.
 * Just trying to avoid code duplication.
 *
 * @author mperever
 */
@FunctionalInterface
public interface SessionQuery
{
    void execute( Session session );
}