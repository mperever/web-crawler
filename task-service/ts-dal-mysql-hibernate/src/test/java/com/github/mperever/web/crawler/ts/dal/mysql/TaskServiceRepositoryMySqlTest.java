package com.github.mperever.web.crawler.ts.dal.mysql;

import com.github.mperever.web.crawler.ts.common.dal.TaskPageTextStats;
import com.github.mperever.web.crawler.ts.common.dal.TaskResultEntities;
import com.github.mperever.web.crawler.ts.common.dto.UrlTask;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TaskServiceRepositoryMySqlTest
{
    private final TaskServiceRepositoryMySql repo = new TaskServiceRepositoryMySql(
            javax.persistence.Persistence.createEntityManagerFactory( "hibernateH2" )
    );

    @Test
    public void get_tasks_for_client()
    {
        final int depthLimit = 1;
        final int errorThreshold = 2;
        final int processTimeOut = 500;
        final int maxCount = 4;
        final String clientId = UUID.randomUUID().toString();

        // 1- Parent
        final String parentUrl = UUID.randomUUID().toString();
        final UrlTask parentTask = new UrlTask( null, parentUrl, 0, true );
        repo.addIfNotExist( parentTask );
        final int parentTaskId = repo.getTask( parentUrl ).getId();

        // 2- External task
        final String childUrl2 = UUID.randomUUID().toString();
        final UrlTask childTask2 = new UrlTask( parentTaskId, childUrl2, 1, true );
        repo.addIfNotExist( childTask2 );
        final int childTask2Id = repo.getTask( parentUrl ).getId();

        // 3 - Task with reached timeout for processing
        final String childUrl3 = UUID.randomUUID().toString();
        final UrlTask childTask3 = new UrlTask( parentTaskId, childUrl3, 1, true );
        final long startTime = Instant.now().toEpochMilli() - processTimeOut;
        childTask3.setClientId( "client1" );
        childTask3.setStartProcessTime( startTime );

        // 4 - Task is finished ('EndProcessTime')
        final String childUrl4 = UUID.randomUUID().toString();
        final UrlTask childTask4 = new UrlTask( parentTaskId, childUrl4, 1, true );
        childTask4.setEndProcessTime( Instant.now().toEpochMilli() );

        // 5 - Task with reached error Threshold
        final String childUrl5 = UUID.randomUUID().toString();
        final UrlTask childTask5 = new UrlTask( parentTaskId, childUrl5, 1, true );
        childTask5.setErrorCount( errorThreshold );

        // 6 - External task with exceed depth limit
        final String childUrl6 = UUID.randomUUID().toString();
        final UrlTask childTask6 = new UrlTask( childTask2Id, childUrl6, 2, true );

        // 7 - Internal task
        final String childUrl7 = UUID.randomUUID().toString();
        final UrlTask childTask7 = new UrlTask( childTask2Id, childUrl7, 2, false );

        // 8 - This internal task will be ignored due to 'maxCount'
        final String childUrl8 = UUID.randomUUID().toString();
        final UrlTask childTask8 = new UrlTask( childTask2Id, childUrl8, 2, false );

        repo.addIfNotExist( childTask3, childTask4, childTask5, childTask6, childTask7, childTask8 );

        final UrlTask[] actualTasks = repo.getTasksForClient(
                clientId,
                maxCount,
                depthLimit,
                processTimeOut,
                errorThreshold );

        Assert.assertEquals( actualTasks.length, maxCount );
        Assert.assertEquals( actualTasks[0], parentTask );
        Assert.assertEquals( actualTasks[1], childTask2 );
        Assert.assertEquals( actualTasks[2], childTask3 );
        Assert.assertEquals( actualTasks[3], childTask7 );
    }

    @Test ( priority = 1 )
    public void add_new_task_and_get_by_url()
    {
        final String url = UUID.randomUUID().toString();
        final UrlTask notExistedTask = new UrlTask( null, url, 0, true );

        repo.addIfNotExist( notExistedTask );

        final UrlTask actualTask = repo.getTask( url );
        Assert.assertEquals( actualTask, notExistedTask );
    }

    @Test ( priority = 1 )
    public void add_multiple_new_tasks()
    {
        final String parentUrl = UUID.randomUUID().toString();
        final UrlTask notExistedParentTask = new UrlTask( null, parentUrl, 0, true );
        repo.addIfNotExist( notExistedParentTask );
        final UrlTask actualParentTask = repo.getTask( parentUrl );

        final int parentTaskId = actualParentTask.getId();
        final String childUrl1 = UUID.randomUUID().toString();
        final String childUrl2 = UUID.randomUUID().toString();
        final UrlTask notExistedChildTask1 = new UrlTask( parentTaskId, childUrl1, 1, true );
        final UrlTask notExistedChildTask2 = new UrlTask( parentTaskId, childUrl2, 1, false );
        repo.addIfNotExist( notExistedChildTask1, notExistedChildTask2 );

        final UrlTask actualChildTask1 = repo.getTask( childUrl1 );
        final UrlTask actualChildTask2 = repo.getTask( childUrl2 );
        Assert.assertEquals( actualChildTask1, notExistedChildTask1 );
        Assert.assertEquals( actualChildTask2, notExistedChildTask2 );
    }

    @Test ( priority = 1 )
    public void add_existed_task()
    {
        final String url = UUID.randomUUID().toString();
        final UrlTask notExistedTask = new UrlTask( null, url, 0, true );

        // Add new task
        final String firstClientId = "client1";
        notExistedTask.setClientId( firstClientId );
        repo.addIfNotExist( notExistedTask );

        // Add the same task with different client id
        final String secondClientId = "client2";
        notExistedTask.setClientId( secondClientId );
        repo.addIfNotExist( notExistedTask );

        final UrlTask actualTask = repo.getTask( url );
        Assert.assertEquals( actualTask.getClientId(), firstClientId );
    }

    @Test ( priority = 1 )
    public void get_not_existed_task()
    {
        final UrlTask actualTask = repo.getTask( UUID.randomUUID().toString() );
        Assert.assertNull( actualTask );
    }

    @Test ( priority = 1 )
    public void update_error_count()
    {
        final String url = UUID.randomUUID().toString();
        final UrlTask notExistedTask = new UrlTask( null, url, 0, true );
        repo.addIfNotExist( notExistedTask );
        final int errorCount = 10;

        repo.updateErrorCount( url, errorCount );

        final UrlTask actualTask = repo.getTask( url );
        Assert.assertEquals( actualTask.getErrorCount(), errorCount );
    }

    @Test ( priority = 1 )
    public void save_task_results()
    {
        // Add new task
        final String url = UUID.randomUUID().toString();
        final UrlTask notExistedTask = new UrlTask( null, url, 0, true );
        repo.addIfNotExist( notExistedTask );
        final int taskId = repo.getTask( url ).getId();

        // Prepare data for TaskResultEntities
        final String childUrl1 = UUID.randomUUID().toString();
        final UrlTask childTask1 = new UrlTask( taskId, childUrl1, 1, false );
        final String childUrl2 = UUID.randomUUID().toString();
        final UrlTask childTask2 = new UrlTask( taskId, childUrl2, 1, true );
        final TaskPageTextStats textStats = new TaskPageTextStats( taskId );
        textStats.setPageText( "page_text" );
        final Map<String,Long> wordStats = new HashMap<>( 2 );
        wordStats.put( "word1", 3L );
        wordStats.put( "word2", 1L );
        textStats.setWordStats( wordStats );

        // Save TaskResultEntities
        final TaskResultEntities taskResult = new TaskResultEntities( taskId );
        taskResult.setTasks( childTask1, childTask2 );
        taskResult.setStats( textStats );
        repo.saveTaskResults( taskResult );

        // Check saved TaskPageTextStats
        final TaskPageTextStats actualTextStats = repo.getPageTextStats( url );
        Assert.assertNotNull( actualTextStats );
        Assert.assertEquals( actualTextStats.getPageText(), textStats.getPageText() );

        // Check updated stage for new task
        final UrlTask updatedActualTask = repo.getTask( url );
        Assert.assertNotEquals( updatedActualTask.getEndProcessTime(), 0 );

        // Check added other new tasks
        final UrlTask actualChildTask1 = repo.getTask( childUrl1 );
        final UrlTask actualChildTask2 = repo.getTask( childUrl2 );
        Assert.assertEquals( actualChildTask1, childTask1 );
        Assert.assertEquals( actualChildTask2, childTask2 );
    }
}