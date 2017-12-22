package com.github.mperever.web.crawler.auth.common.dto;

import java.io.Serializable;

/**
 * Represents credentials for user authentication.
 *
 * @author mperever
 */
public final class Credentials implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private Credentials()
    {
    }

    public Credentials( String userName, String password )
    {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUserName()
    {
        return userName;
    }
}