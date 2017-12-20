package com.github.mperever.web.crawler.ts.common.dto;

import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.common.json.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TaskServiceDtoSerializationTest
{
    private final JsonSerializer serializer = new JacksonJsonSerializer();

    @Test
    public void urlTask_serialize()
    {
        final UrlTask task = new UrlTask( 1, "url", 0, true );
        task.setClientId( UUID.randomUUID().toString() );
        task.setStartProcessTime( 100 );
        task.setEndProcessTime( 200 );
        task.setErrorCount( 10 );
        task.setId( 2 );

        final String jsonTask = serializer.encode( task );
        final UrlTask actualTask = serializer.decode( jsonTask, UrlTask.class );

        Assert.assertEquals( actualTask, task );
    }

    @Test
    public void taskResults_serialize()
    {
        final TaskResults taskResults = new TaskResults();
        taskResults.setNewUrls( new String[]{ "url1", "url2" } );
        taskResults.setPageText( "TEXT" );
        final Map<String,Long> wordStats = new HashMap<>();
        wordStats.put( "word1", 2L );
        wordStats.put( "word2", 5L );
        taskResults.setWordsStats( wordStats );

        final String jsonTaskResults = serializer.encode( taskResults );
        final TaskResults actualTaskResults = serializer.decode( jsonTaskResults, TaskResults.class );

        Assert.assertEquals( actualTaskResults.getNewUrls(), taskResults.getNewUrls() );
        Assert.assertEquals( actualTaskResults.getPageText(), taskResults.getPageText() );
        Assert.assertEquals( actualTaskResults.getWordsStats(), taskResults.getWordsStats() );
    }
}