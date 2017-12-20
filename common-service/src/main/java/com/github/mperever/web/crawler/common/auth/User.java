package com.github.mperever.web.crawler.common.auth;

import java.io.Serializable;

/**
 * Represents full information about an user to save and update authorisation service.
 *
 * @author mperever
 */
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private String password;
    private Role role;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private User()
    {
    }

    public User( String name, String password, Role role )
    {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public String getName()
    {
        return name;
    }

    public String getPassword()
    {
        return password;
    }

    public Role getRole()
    {
        return role;
    }
}