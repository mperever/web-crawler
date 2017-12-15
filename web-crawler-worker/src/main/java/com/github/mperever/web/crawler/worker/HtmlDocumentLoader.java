package com.github.mperever.web.crawler.worker;

import java.net.URI;

/**
 * Represents interface for loading Html document from internet (pure function).
 *
 * @author mperever
 */
@FunctionalInterface
public interface HtmlDocumentLoader
{
    /**
     * Creates new instance of {@link HtmlDocument}.
     *
     * @param url The link url fo loading
     * @return The content object
     */
    HtmlDocument load( URI url );
}