package com.github.mperever.dal.mysql;

import com.github.mperever.common.dal.TaskPageTextStats;
import com.github.mperever.common.dal.TaskResultEntities;
import com.github.mperever.common.dal.TaskServiceRepository;
import com.github.mperever.common.dto.UrlTask;

import com.github.mperever.dal.mysql.internal.SessionQueryResult;
import com.github.mperever.dal.mysql.internal.UrlTask_;
import com.github.mperever.dal.mysql.internal.SessionFactoryHolder;
import com.github.mperever.dal.mysql.internal.SessionQuery;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

    private final SessionFactory sessionFactory;

    TaskServiceRepositoryMySql( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    public TaskServiceRepositoryMySql()
    {
        this( SessionFactoryHolder.getSessionFactory() );
    }

    @Override
    public void addIfNotExist( UrlTask... tasks )
    {
        this.executeQueries( session -> this.addIfNotExist( session, tasks ) );
    }

    private void addIfNotExist( final Session session, final UrlTask... tasks )
    {
        final List<UrlTask> existedTasks = this.getExistedTasks( session, tasks );
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
                session.persist( task );
                logger.info( "The task has been added : {}", task );
            }
        }
    }

    private List<UrlTask> getExistedTasks( final Session session, final UrlTask... tasks )
    {
        final CriteriaBuilder builder = session.getCriteriaBuilder();
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
        final Query<UrlTask> query = session.createQuery( selectCriteria );

        return query.getResultList();
    }

    @Override
    public TaskPageTextStats getPageTextStats( String url )
    {
        return this.executeQueriesResult( session -> this.getPageTextStats( session, url ) );
    }

    private TaskPageTextStats getPageTextStats( final Session session, String url )
    {
        final UrlTask task = this.getTaskByUrl( session, url );
        if ( task == null )
        {
            return null;
        }
        final int taskId = task.getId();

        return session.get( TaskPageTextStats.class, taskId );
    }

    @Override
    public void saveTaskResults( final TaskResultEntities taskResults )
    {
        this.executeQueries( session -> this.saveTaskResults( session, taskResults ) );
    }

    private void saveTaskResults( final Session session, final TaskResultEntities taskResults )
    {
        // Add new tasks
        final UrlTask[] tasks = taskResults.getTasks();
        if ( tasks != null && tasks.length != 0 )
        {
            this.addIfNotExist( session, tasks );
        }
        // Add page text and text stats
        final TaskPageTextStats textStats = taskResults.getStats();
        if ( textStats != null )
        {
            session.persist( taskResults.getStats() );
            logger.debug( "The page text and word stats have been added for task with id: {}", textStats.getTaskId() );
        }
        // Set task end process time
        final long endProcessTime = Instant.now().toEpochMilli();
        setEndProcessTime( session, taskResults.getTaskId(), endProcessTime );
    }

    private void setEndProcessTime( final Session session, int id, long endTime )
    {
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaUpdate<UrlTask> updateCriteria = builder.createCriteriaUpdate( UrlTask.class );

        // Create expression for where clause to find task by id
        final Root<UrlTask> tasksRoot = updateCriteria.from( UrlTask.class );
        final Predicate byId = builder.equal( tasksRoot.get( UrlTask_.id ), id );

        // Create and run query based on search criteria
        updateCriteria.set( UrlTask_.endProcessTime, endTime )
                .where( byId );

        boolean isUpdated = session.createQuery( updateCriteria ).executeUpdate() != 0;
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
        this.executeQueries( session -> this.updateErrorCount( session, url, errorCount ) );
    }

    private void updateErrorCount( final Session session, String url, int errorCount )
    {
        final UrlTask task = this.getTaskByUrl( session, url );
        if ( task == null )
        {
            logger.error( "Error in updating error count. Could not find task by url: " + url );
            return;
        }
        task.setErrorCount( errorCount );
        session.update( task );
        logger.debug( "Error count has been changed to '{}' for url {}", errorCount, url );
    }

    @Override
    public UrlTask getTask( String url )
    {
        return this.executeQueriesResult( session -> this.getTaskByUrl( session, url ) );
    }

    private UrlTask getTaskByUrl( final Session session, String url )
    {
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaQuery<UrlTask> selectCriteria = builder.createQuery( UrlTask.class );

        final Root<UrlTask> tasksRoot = selectCriteria.from( UrlTask.class );

        // Create expression for where clause to find task by url value
        final Predicate byUrl = builder.equal( tasksRoot.get( UrlTask_.url ), url );

        // Create and run query based on search selectCriteria
        selectCriteria.select( tasksRoot )
                .where( byUrl );
        final Query<UrlTask> query = session.createQuery( selectCriteria );

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
        return this.executeQueriesResult( session ->
                this.getFreeTasks( session, clientId, maxCount, depthLimit, timeOutInMs, errorThreshold ) );
    }

    private UrlTask[] getFreeTasks( final Session session,
                                    String clientId,
                                    int maxCount,
                                    int depthLimit,
                                    long timeOutInMs,
                                    int errorThreshold )
    {
        final CriteriaBuilder builder = session.getCriteriaBuilder();
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
        final Query<UrlTask> query = session.createQuery( criteria ).setMaxResults( maxCount );

        // Convert query results into array of tasks
        final List<UrlTask> queryResult = query.getResultList();
        final UrlTask[] resultTasks = queryResult.toArray( new UrlTask[ queryResult.size() ] );

        final long nowTime = Instant.now().toEpochMilli();
        assignTasksToClient( session, resultTasks, clientId, nowTime );
        return resultTasks;
    }

    private static void assignTasksToClient( final Session session,
                                             final UrlTask[] tasks,
                                             String clientId,
                                             long startProcTime )
    {
        for ( UrlTask task : tasks )
        {
            task.setClientId( clientId );
            task.setStartProcessTime( startProcTime );
            session.update( task );
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

    private void executeQueries( final SessionQuery query )
    {
        Transaction transaction = null;
        try ( final Session session = sessionFactory.openSession() )
        {
            transaction = session.beginTransaction();
            query.execute( session );
            transaction.commit();

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            if ( transaction != null )
            {
                transaction.rollback();
            }
        }
    }

    private <T> T executeQueriesResult( final SessionQueryResult query )
    {
        T result = null;
        Transaction transaction = null;
        try ( final Session session = sessionFactory.openSession() )
        {
            transaction = session.beginTransaction();
            result = ( T ) query.execute( session );
            transaction.commit();

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
            if ( transaction != null )
            {
                transaction.rollback();
            }
        }
        return result;
    }
}