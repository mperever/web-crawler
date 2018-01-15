package com.github.mperever.web.crawler.auth.dal.mysql;

import com.github.mperever.web.crawler.auth.dal.AuthServiceRepository;
import com.github.mperever.web.crawler.auth.common.dto.User;

import com.github.mperever.web.crawler.auth.dal.mysql.internal.EntityManagerFactoryHolder;
import com.github.mperever.web.crawler.auth.dal.mysql.internal.EntityManagerQuery;
import com.github.mperever.web.crawler.auth.dal.mysql.internal.EntityManagerQueryResult;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the MySql implementation for {@link AuthServiceRepository}.
 *
 * @author mperever
 */
public class AuthServiceRepositoryMySql implements AuthServiceRepository
{
    private static final Logger logger = LoggerFactory.getLogger( AuthServiceRepositoryMySql.class );

    private final EntityManagerFactory entityManagerFactory;

    AuthServiceRepositoryMySql( EntityManagerFactory entityManagerFactory )
    {
        this.entityManagerFactory = entityManagerFactory;
    }

    public AuthServiceRepositoryMySql()
    {
        this( EntityManagerFactoryHolder.ENTITY_MANAGER_FACTORY );
    }

    @Override
    public void addUserIfNotExist( User newUser )
    {
        this.executeQueries( entityManager -> this.addUserIfNotExist( entityManager, newUser ) );
    }

    private void addUserIfNotExist( final EntityManager entityManager, User newUser )
    {
        final String userName = newUser.getName();
        final boolean isUserExist = entityManager.find( User.class, userName ) != null;
        if ( isUserExist )
        {
            logger.warn( "User '{}' already exists", newUser );
            return;
        }

        entityManager.persist( newUser );
    }

    @Override
    public User getUserByName( String userName )
    {
        return this.executeQueriesResult( entityManager -> entityManager.find( User.class, userName ) );
    }

    @Override
    public List<User> getUsers( int offset, int limit )
    {
        return this.executeQueriesResult( entityManager -> getUsers( entityManager, offset, limit ) );
    }

    private List<User> getUsers( final EntityManager entityManager, int offset, int limit )
    {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> selectCriteria = builder.createQuery( User.class );

        final Root<User> usersRoot = selectCriteria.from( User.class );
        selectCriteria.select( usersRoot );

        return entityManager.createQuery( selectCriteria )
                .setFirstResult( offset )
                .setMaxResults( limit )
                .getResultList();
    }

    @Override
    public void updateUser( User user )
    {
        this.executeQueries( entityManager -> entityManager.merge( user ) );
    }

    @Override
    public void deleteUser( String userName )
    {
        this.executeQueries( entityManager -> this.deleteUser( entityManager, userName ) );
    }

    private void deleteUser( final EntityManager entityManager, String userName )
    {
        final User userToDelete = getUserByName( userName );
        final boolean isUserExist = userToDelete != null;
        if ( !isUserExist )
        {
            logger.warn( "User '{}' does not exist.", userName );
            return;
        }
        entityManager.remove( userToDelete );
    }

    private void executeQueries( final EntityManagerQuery query )
    {
        EntityTransaction transaction = null;
        EntityManager manager = null;
        try
        {
            manager = entityManagerFactory.createEntityManager();

            transaction = manager.getTransaction();
            transaction.begin();
            query.execute( manager );
            transaction.commit();

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            if ( transaction != null )
            {
                transaction.rollback();
            }
        }
        finally
        {
            if ( manager != null )
            {
                manager.close();
            }
        }
    }

    private <T> T executeQueriesResult( final EntityManagerQueryResult query )
    {
        T result = null;
        EntityTransaction transaction = null;
        EntityManager manager = null;
        try
        {
            manager = entityManagerFactory.createEntityManager();

            transaction = manager.getTransaction();
            result = ( T ) query.execute( manager );
            transaction.commit();

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            if ( transaction != null )
            {
                transaction.rollback();
            }
        }
        finally
        {
            if ( manager != null )
            {
                manager.close();
            }
        }

        return result;
    }
}