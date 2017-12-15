package com.github.mperever.web.crawler.worker.internal;

import com.github.mperever.web.crawler.worker.HtmlDocument;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jsoup.nodes.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents implementation for HTML document using jsoup API.
 *
 * @author mperever
 */
class JsoupHtmlDocument implements HtmlDocument
{
    private static final Logger logger = LoggerFactory.getLogger( JsoupHtmlDocument.class );

    private String plainText;
    private Set<URI> urls;

    private final Document htmlDoc;

    JsoupHtmlDocument( Document htmlDoc )
    {
        this.htmlDoc = htmlDoc;
    }

    @Override
    public String getPlainText()
    {
        if ( plainText == null )
        {
            plainText = htmlDoc.select( "body" ).text();
        }

        return plainText;
    }

    @Override
    public Set<URI> getUniqueLinks()
    {
        if ( urls == null )
        {
            urls = new HashSet<>();

            final List<String> hrefs = htmlDoc
                    .select( "a[href]" )
                    .eachAttr("abs:href" );

            for ( String href : hrefs )
            {
                logger.debug( "Analysing HTML link with href '" + href + "'" );
                final URI linkUri = hrefToURI( href );

                if ( linkUri != null && isLinkSupported( linkUri ) )
                {
                    urls.add( linkUri );
                    logger.debug( "Add link into result set. URL: '" + linkUri + "'" );
                }
            }
        }

        return urls;
    }

    private static URI hrefToURI( final String hrefAttrValue )
    {
        String href = removeHtmlBookmark( hrefAttrValue );
        href = removeLastSlash( href );
        if ( "".equals( href ) )
        {
            return null;
        }

        try
        {
            return new URI( href );

        } catch ( URISyntaxException ex )
        {
            logger.debug( "Could not convert the link '" + href + "' to URI.", ex );
        }
        return null;
    }

    private static boolean isLinkSupported( final URI url )
    {
        final String urlProtocol = url.getScheme().toLowerCase( Locale.ENGLISH );
        final boolean isProtocolSupported = "http".equals( urlProtocol ) || "https".equals( urlProtocol );

        if ( !isProtocolSupported )
        {
            logger.debug( "The protocol '{}' is not supported by crawler.", urlProtocol );
        }

        if ( url.getHost() == null )
        {
            logger.debug( "The link host is null. url '{}'", url );
        }

        return url.getHost() != null && isProtocolSupported;
    }

    private static String removeHtmlBookmark( final String htmlLink )
    {
        final int hashPosition = htmlLink.indexOf( '#' );
        return hashPosition != -1
                ? htmlLink.substring( 0, hashPosition )
                : htmlLink;
    }

    private static String removeLastSlash( final String htmlLink )
    {
        if ( htmlLink == null || htmlLink.isEmpty() )
        {
            return htmlLink;
        }
        // Remove last character if it's '/'
        return htmlLink.charAt( htmlLink.length() - 1 ) == '/'
                ? htmlLink.substring( 0, htmlLink.length() - 1 )
                : htmlLink;
    }
}