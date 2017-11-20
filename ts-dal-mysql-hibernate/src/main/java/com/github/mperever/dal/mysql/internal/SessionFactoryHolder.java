package com.github.mperever.dal.mysql.internal;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents singleton to keep hibernate session factory once for an application.
 *
 * @author mperever
 */
public class SessionFactoryHolder
{
    private static final Logger logger = LoggerFactory.getLogger( SessionFactoryHolder.class );

    private static SessionFactory sessionFactory;

    private SessionFactoryHolder()
    {
    }

    public static SessionFactory getSessionFactory()
    {
        if ( sessionFactory == null )
        {
            initSessionFactory();
        }
        return sessionFactory;
    }

    private static void initSessionFactory()
    {
        // configures settings from hibernate.cfg.xml
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try
        {
            sessionFactory = new MetadataSources( registry )
                    .buildMetadata()
                    .buildSessionFactory();
        }
        catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );

            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }
}