package com.github.mperever.web.crawler.auth.common.utils;

import java.security.NoSuchAlgorithmException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DigestUtilsTest
{
    @Test
    public void create_hash_with_salt_in_resources() throws NoSuchAlgorithmException
    {
        final String passwordHash = DigestUtils.hash( "testUser", "some_password" );
        Assert.assertNotNull( passwordHash );
    }
}