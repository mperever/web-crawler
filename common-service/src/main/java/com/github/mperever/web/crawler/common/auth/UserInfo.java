package com.github.mperever.web.crawler.common.auth;

import java.io.Serializable;

/**
 *  Represents user information without sensitive data (password).
 *
 * @author mperever
 */
public class UserInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private Role role;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private UserInfo()
    {
    }

    public UserInfo( String name, Role role )
    {
        this.name = name;
        this.role = role;
    }

    public String getName()
    {
        return name;
    }

    public Role getRole()
    {
        return role;
    }
}