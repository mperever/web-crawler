package com.github.mperever.web.crawler.auth.common.dto;

import java.io.Serializable;
import java.security.Principal;

/**
 *  Represents user name and role.
 *
 * @author mperever
 */
public class UserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 1L;

    private String name;
    private Role role;

    /**
     *  This constructor is added as prerequisite for serialization.
     */
    private UserPrincipal()
    {
    }

    public UserPrincipal( String name, Role role )
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