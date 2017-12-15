package com.github.mperever.web.crawler.ts.dal.mysql;

import com.github.mperever.web.crawler.ts.common.dal.TaskPageTextStats;
import com.github.mperever.web.crawler.ts.common.dal.TaskResultEntities;
import com.github.mperever.web.crawler.ts.common.dal.TaskServiceRepository;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import com.github.mperever.web.crawler.ts.dal.mysql.internal.EntityManagerFactoryHolder;
import com.github.mperever.web.crawler.ts.dal.mysql.internal.EntityManagerQuery;
import com.github.mperever.web.crawler.ts.dal.mysql.internal.EntityManagerQueryResult;
import com.github.mperever.web.crawler.ts.dal.mysql.internal.UrlTask_;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the MySql implementation for {@link TaskServiceRepository}.
 *
 * @author mperever
 */
public class TaskServiceRepositoryMySql implements TaskServiceRepository
{
    private static final Logger logger = LoggerFactory.getLogger( TaskServiceRepositoryMySql.class );

    private final EntityManagerFactory entityManagerFactory;

    TaskServiceRepositoryMySql( EntityManagerFactory entityManagerFactory )
    {
        this.entityManagerFactory = entityManagerFactory;
    }

    public TaskServiceRepositoryMySql()
    {
        this( EntityManagerFactoryHolder.ENTITY_MANAGER_FACTORY );
    }

    @Override
    public void addIfNotExist( UrlTask... tasks )
    {
        this.executeQueries( entityManager -> this.addIfNotExist( entityManager, tasks ) );
    }

    private void addIfNotExist( final EntityManager entityManager, final UrlTask... tasks )
    {
        final List<UrlTask> existedTasks = this.getExistedTasks( entityManager, tasks );
        for ( UrlTask task : tasks )
        {
            final int existedTaskIndex = existedTasks.indexOf( task );
            final boolean isExist = existedTaskIndex >= 0;
            if ( isExist )
            {
                logger.debug( "The task already exist: {}", existedTasks.get( existedTaskIndex ) );
            }
            else
            {
                entityManager.persist( task );
                logger.info( "The task has been added : {}", task );
            }
        }
    }

    private List<UrlTask> getExistedTasks( final EntityManager entityManager, final UrlTask... tasks )
    {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UrlTask> selectCriteria = builder.createQuery( UrlTask.class );

        final Root<UrlTask> tasksRoot = selectCriteria.from( UrlTask.class );

        // Build multiple byUrls for where clause to select tasks by urls
        final List<Predicate> byUrls = new ArrayList<>();
        for ( UrlTask task : tasks )
        {
            byUrls.add( builder.equal( tasksRoot.get( UrlTask_.url ), task.getUrl() ) );
        }
        final Predicate byOrUrls = builder.or( byUrls.toArray( new Predicate[byUrls.size()] ) );

        // Create and run query based on search selectCriteria
        selectCriteria.select( tasksRoot )
                .where( byOrUrls );
        final TypedQuery<UrlTask> query = entityManager.createQuery( selectCriteria );

        return query.getResultList();
    }

    @Override
    public TaskPageTextStats getPageTextStats( String url )
    {
        return this.executeQueriesResult( entityManager -> this.getPageTextStats( entityManager, url ) );
    }

    private TaskPageTextStats getPageTextStats( final EntityManager entityManager, String url )
    {
        final UrlTask task = this.getTaskByUrl( entityManager, url );
        if ( task == null )
        {
            return null;
        }
        final int taskId = task.getId();

        return entityManager.find( TaskPageTextStats.class, taskId );
    }

    @Override
    public void saveTaskResults( final TaskResultEntities taskResults )
    {
        this.executeQueries( entityManager -> this.saveTaskResults( entityManager, taskResults ) );
    }

    private void saveTaskResults( final EntityManager entityManager, final TaskResultEntities taskResults )
    {
        // Add new tasks
        final UrlTask[] tasks = taskResults.getTasks();
        if ( tasks != null && tasks.length != 0 )
        {
            this.addIfNotExist( entityManager, tasks );
        }
        // Add page text and text stats
        final TaskPageTextStats textStats = taskResults.getStats();
        if ( textStats != null )
        {
            entityManager.persist( taskResults.getStats() );
            logger.debug( "The page text and word stats have been added for task with id: {}", textStats.getTaskId() );
        }
        // Set task end process time
        final long endProcessTime = Instant.now().toEpochMilli();
        setEndProcessTime( entityManager, taskResults.getTaskId(), endProcessTime );
    }

    private void setEndProcessTime( final EntityManager entityManager, int id, long endTime )
    {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaUpdate<UrlTask> updateCriteria = builder.createCriteriaUpdate( UrlTask.class );

        // Create expression for where clause to find task by id
        final Root<UrlTask> tasksRoot = updateCriteria.from( UrlTask.class );
        final Predicate byId = builder.equal( tasksRoot.get( UrlTask_.id ), id );

        // Create and run query based on search criteria
        updateCriteria.set( UrlTask_.endProcessTime, endTime )
                .where( byId );

        boolean isUpdated = entityManager.createQuery( updateCriteria ).executeUpdate() != 0;
        if ( isUpdated )
        {
            logger.debug( "End process time of task with id '{}' has been set to '{}'", id, endTime );
        }
        else
        {
            logger.error( "Fail. End process time of task with id '{}' has NOT been set to '{}'", id, endTime );
        }
    }

    @Override
    public void updateErrorCount( String url, int errorCount )
    {
        this.executeQueries( entityManager -> this.updateErrorCount( entityManager, url, errorCount ) );
    }

    private void updateErrorCount( final EntityManager entityManager, String url, int errorCount )
    {
        final UrlTask task = this.getTaskByUrl( entityManager, url );
        if ( task == null )
        {
            logger.error( "Error in updating error count. Could not find task by url: " + url );
            return;
        }
        task.setErrorCount( errorCount );
        entityManager.merge( task );
        logger.debug( "Error count has been changed to '{}' for url {}", errorCount, url );
    }

    @Override
    public UrlTask getTask( String url )
    {
        return this.executeQueriesResult( entityManager -> this.getTaskByUrl( entityManager, url ) );
    }

    private UrlTask getTaskByUrl( final EntityManager entityManager, String url )
    {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UrlTask> selectCriteria = builder.createQuery( UrlTask.class );

        final Root<UrlTask> tasksRoot = selectCriteria.from( UrlTask.class );

        // Create expression for where clause to find task by url value
        final Predicate byUrl = builder.equal( tasksRoot.get( UrlTask_.url ), url );

        // Create and run query based on search selectCriteria
        selectCriteria.select( tasksRoot )
                .where( byUrl );
        final TypedQuery<UrlTask> query = entityManager.createQuery( selectCriteria );

        final List<UrlTask> queryResult = query.getResultList();
        return queryResult.isEmpty() ? null : queryResult.get( 0 );
    }

    @Override
    public UrlTask[] getTasksForClient( String clientId,
                                        int maxCount,
                                        int depthLimit,
                                        long timeOutInMs,
                                        int errorThreshold )
    {
        return this.executeQueriesResult( entityManager ->
                this.getFreeTasks( entityManager, clientId, maxCount, depthLimit, timeOutInMs, errorThreshold ) );
    }

    private UrlTask[] getFreeTasks( final EntityManager entityManager,
                                    String clientId,
                                    int maxCount,
                                    int depthLimit,
                                    long timeOutInMs,
                                    int errorThreshold )
    {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UrlTask> criteria = builder.createQuery( UrlTask.class );
        final Root<UrlTask> tasksRoot = criteria.from( UrlTask.class );

        // Build multiple expressions for where clause to get task for processing
        final Predicate taskCanBeTaken = createTaskCanBeTaken(
                builder,
                tasksRoot,
                depthLimit,
                timeOutInMs,
                errorThreshold );

        // Create and run query based on search criteria
        criteria.select( tasksRoot )
                .where( taskCanBeTaken );
        final TypedQuery<UrlTask> query = entityManager.createQuery( criteria ).setMaxResults( maxCount );

        // Convert query results into array of tasks
        final List<UrlTask> queryResult = query.getResultList();
        final UrlTask[] resultTasks = queryResult.toArray( new UrlTask[ queryResult.size() ] );

        final long nowTime = Instant.now().toEpochMilli();
        assignTasksToClient( entityManager, resultTasks, clientId, nowTime );
        return resultTasks;
    }

    private static void assignTasksToClient( final EntityManager entityManager,
                                             final UrlTask[] tasks,
                                             String clientId,
                                             long startProcTime )
    {
        for ( UrlTask task : tasks )
        {
            task.setClientId( clientId );
            task.setStartProcessTime( startProcTime );
            entityManager.merge( task );
        }
    }

    /**
     * Creates {@link Predicate} for condition:
     *
     *     task.getEndProcessTime() == 0
     *     && ( task.getErrorCount() < errorThreshold )
     *     && ( !task.isExternal() || task.getDepth() <= depthLimit )
     *     && ( task.getClientId() == null || task.getStartProcessTime() < nowTime - timeOutInMs )
     */
    private static Predicate createTaskCanBeTaken( final CriteriaBuilder builder,
                                                   final Root<UrlTask> tasksRoot,
                                                   int depthLimit,
                                                   long timeOutInMs,
                                                   int errorThreshold )
    {
        final Predicate taskNotFinished = builder.equal(
                tasksRoot.get( UrlTask_.endProcessTime ), 0 );
        final Predicate errorCountLessThreshold = builder.lt(
                tasksRoot.get( UrlTask_.errorCount ),
                errorThreshold );
        final Predicate depthCompliance = createDepthCompliance( builder, tasksRoot, depthLimit );
        final long nowTime = Instant.now().toEpochMilli();
        final Predicate taskIsNotProcessing = createTaskIsNotProcessing( builder, tasksRoot, nowTime, timeOutInMs );

        return builder.and(
                taskNotFinished,
                errorCountLessThreshold,
                depthCompliance,
                taskIsNotProcessing );
    }

    /**
     * Creates {@link Predicate} for condition:
     *
     *   !task.isExternal() || task.getDepth() <= depthLimit
     */
    private static Predicate createDepthCompliance( final CriteriaBuilder builder,
                                                    final Root<UrlTask> tasksRoot,
                                                    int depthLimit )
    {
        final Predicate isInternal = builder
                .isFalse( tasksRoot.get( UrlTask_.external ) );
        final Predicate depthLimitCompliance = builder
                .le( tasksRoot.get( UrlTask_.depth ), depthLimit );

        return builder.or( isInternal, depthLimitCompliance );
    }

    /**
     * Creates {@link Predicate} for condition:
     *
     *   task.getClientId() == null || task.getStartProcessTime() < nowTime - timeOutInMs
     */
    private static Predicate createTaskIsNotProcessing( final CriteriaBuilder builder,
                                                        final Root<UrlTask> tasksRoot,
                                                        long nowTime,
                                                        long timeOutInMs )
    {
        final Predicate notAssignedToProcessor = builder
                .isNull( tasksRoot.get( UrlTask_.clientId ) );
        final Predicate processingTimeExpired = builder
                .lt( tasksRoot.get( UrlTask_.startProcessTime ),
                nowTime - timeOutInMs );

        return builder.or( notAssignedToProcessor, processingTimeExpired );
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