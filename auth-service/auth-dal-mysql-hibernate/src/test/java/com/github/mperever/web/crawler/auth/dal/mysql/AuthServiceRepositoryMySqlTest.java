package com.github.mperever.web.crawler.auth.dal.mysql;

import com.github.mperever.web.crawler.auth.common.dto.Role;
import com.github.mperever.web.crawler.auth.common.dto.User;

import org.testng.annotations.Test;

// TODO: Add tests

public class AuthServiceRepositoryMySqlTest
{
    private final AuthServiceRepositoryMySql repo = new AuthServiceRepositoryMySql(
            javax.persistence.Persistence.createEntityManagerFactory( "hibernateH2" )
    );

    @Test
    public void test()
    {
        final User userToAdd = new User( "test", "password", Role.ADMIN );
        repo.addUserIfNotExist( userToAdd );
    }
}