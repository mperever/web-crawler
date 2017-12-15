package com.github.mperever.web.crawler.ts.dal.mysql.internal;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Represents singleton to keep JPA entity manager factory once for an application.
 *
 * @author mperever
 */
public class EntityManagerFactoryHolder
{
    // configures settings from META-INF/persistence.xml
    public final static EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory( "hibernateMySql" );

    private EntityManagerFactoryHolder()
    {
    }

    @Override
    protected void finalize() throws Throwable {
        try
        {
            ENTITY_MANAGER_FACTORY.close();
        } finally
        {
            super.finalize();
        }
    }
}