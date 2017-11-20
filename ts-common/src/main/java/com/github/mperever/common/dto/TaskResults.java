package com.github.mperever.common.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents task results for URL.
 *
 * @author mperever
 */
@SuppressFBWarnings( { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" } )
public class TaskResults implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String[] newUrls;
    private String pageText;
    private Map<String, Long> wordsStats;

    public String[] getNewUrls()
    {
        return newUrls;
    }

    public void setNewUrls( String[] newUrls )
    {
        this.newUrls = newUrls;
    }

    public String getPageText()
    {
        return pageText;
    }

    public void setPageText( String pageText )
    {
        this.pageText = pageText;
    }

    public Map<String, Long> getWordsStats()
    {
        return wordsStats;
    }

    public void setWordsStats( Map<String, Long> wordsStats )
    {
        this.wordsStats = wordsStats;
    }
}