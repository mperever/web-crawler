package com.github.mperever.worker.internal;

import com.github.mperever.worker.HtmlDocument;
import com.github.mperever.worker.HtmlDocumentLoader;

import java.io.IOException;
import java.net.URI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for loading Html document from internet using jsoup API.
 *
 * @author mperever
 */
public class JsoupHtmlDocumentLoader implements HtmlDocumentLoader
{
    private static final Logger logger = LoggerFactory.getLogger( JsoupHtmlDocumentLoader.class );

    @Override
    public HtmlDocument load( final URI url )
    {
        if ( url == null )
        {
            throw new IllegalArgumentException( "parameter 'url' is null." );
        }

        try
        {
            final Document htmlDoc = Jsoup.connect( url.toString() ).get();

            return new JsoupHtmlDocument( htmlDoc );

        } catch ( IOException ex )
        {
            logger.error( ex.getMessage(), ex );

            throw new RuntimeException( ex );
        }
    }
}