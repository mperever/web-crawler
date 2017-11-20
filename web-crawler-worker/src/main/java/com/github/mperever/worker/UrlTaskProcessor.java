package com.github.mperever.worker;

import com.github.mperever.common.dto.TaskResults;
import com.github.mperever.worker.internal.JsoupHtmlDocumentLoader;
import com.github.mperever.worker.internal.TextStatsCalculator;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents facade for task processing.
 *
 * @author mperever
 */
public class UrlTaskProcessor
{
    private static final Logger logger = LoggerFactory.getLogger( UrlTaskProcessor.class );

    private final HtmlDocumentLoader loader;

    UrlTaskProcessor( HtmlDocumentLoader loader )
    {
        this.loader = loader;
    }

    public UrlTaskProcessor()
    {
        this( new JsoupHtmlDocumentLoader() );
    }

    public TaskResults process( final String taskUrl )
    {
        try
        {
            final HtmlDocument document = loader.load( new URI( taskUrl ) );
            final TaskResults taskResults = new TaskResults();

            final String[] linkUrls = document.getUniqueLinks().stream()
                    .map( URI::toString )
                    .toArray( String[]::new );
            taskResults.setNewUrls( linkUrls );

            final String plainText = document.getPlainText();
            taskResults.setPageText( plainText );

            final Map<String,Long> wordStats = TextStatsCalculator.calculateWordStatistic( plainText );
            taskResults.setWordsStats( wordStats );

            return taskResults;

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
        }

        return null;
    }
}