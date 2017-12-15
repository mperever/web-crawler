package com.github.mperever.common.dal;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents page text and word statistic as a part of task results.
 *
 * @author mperever
 */
public class TaskPageTextStats implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int taskId;
    private String pageText;
    private Map<String,Long> wordStats;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private TaskPageTextStats()
    {
    }

    public TaskPageTextStats( int taskId )
    {
        this.taskId = taskId;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId( int taskId )
    {
        this.taskId = taskId;
    }

    public String getPageText()
    {
        return pageText;
    }

    public void setPageText( String pageText )
    {
        this.pageText = pageText;
    }

    public Map<String, Long> getWordStats()
    {
        return wordStats;
    }

    public void setWordStats( Map<String, Long> wordStats )
    {
        this.wordStats = wordStats;
    }
}