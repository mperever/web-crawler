package com.github.mperever.worker;

import java.net.URI;
import java.util.Set;

/**
 * Represents interface for HTML document.
 *
 * @author mperever
 */
public interface HtmlDocument
{
    /**
     *  Extracts plain text from HTML document.
     *
     * @return plain text
     */
    String getPlainText();

    /**
     * Extracts HTML links from HTML document.
     *
     * @return HTML links
     */
    Set<URI> getUniqueLinks();
}