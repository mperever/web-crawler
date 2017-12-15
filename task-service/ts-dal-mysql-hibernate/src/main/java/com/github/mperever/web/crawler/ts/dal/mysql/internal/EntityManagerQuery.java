package com.github.mperever.web.crawler.ts.dal.mysql.internal;

import javax.persistence.EntityManager;

/**
 * Represents the interface to perform queries in on {@link EntityManager}.
 * Just trying to avoid code duplication.
 *
 * @author mperever
 */
@FunctionalInterface
public interface EntityManagerQuery
{
    void execute( EntityManager session );
}