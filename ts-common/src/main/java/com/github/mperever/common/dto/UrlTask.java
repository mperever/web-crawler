package com.github.mperever.common.dto;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents task for processing URL.
 *
 * @author mperever
 */
public class UrlTask implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer parentId;
    private int id;
    private String url;
    private int depth;
    private boolean external;
    private long startProcessTime;
    private long endProcessTime;
    private int errorCount;
    private String clientId;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private UrlTask()
    {
    }

    public UrlTask( Integer parentId, String url, int depth, boolean external )
    {
        this.parentId = parentId;
        this.url = url;
        this.depth = depth;
        this.external = external;
    }

    public UrlTask( UrlTask parent, String url ) throws URISyntaxException
    {
        final URI parentUrl = new URI( parent.getUrl() );
        final URI taskUrl = new URI( url );

        this.external = isExternal( taskUrl, parentUrl );
        this.parentId = parent.getId();
        this.url = url;
        this.depth = parent.getDepth() + 1;
    }

    private static boolean isExternal( final URI url, final URI parentUrl )
    {
        return !( url.getScheme().equals( parentUrl.getScheme() )
                && url.getHost().equals( parentUrl.getHost() )
                && url.getPort() == parentUrl.getPort() );
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public int getDepth()
    {
        return depth;
    }

    public boolean isExternal()
    {
        return external;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId( String clientId )
    {
        this.clientId = clientId;
    }

    public long getStartProcessTime()
    {
        return startProcessTime;
    }

    public void setStartProcessTime( long startProcessTime )
    {
        this.startProcessTime = startProcessTime;
    }

    public long getEndProcessTime()
    {
        return endProcessTime;
    }

    public void setEndProcessTime( long endProcessTime )
    {
        this.endProcessTime = endProcessTime;
    }

    public int getErrorCount()
    {
        return errorCount;
    }

    public void setErrorCount( int errorCount )
    {
        this.errorCount = errorCount;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( this.getClass() != obj.getClass() )
        {
            return false;
        }

        final UrlTask other = ( UrlTask ) obj;
        return this.url.equals( other.url );
    }

    @Override
    public int hashCode()
    {
        return url.hashCode();
    }

    @Override
    public String toString()
    {
        return String.format( "[%s, %s, %s, %s, %s, %s, %s, %s, %s]",
                id,
                url,
                depth,
                external,
                errorCount,
                startProcessTime,
                endProcessTime,
                clientId,
                parentId );
    }
}