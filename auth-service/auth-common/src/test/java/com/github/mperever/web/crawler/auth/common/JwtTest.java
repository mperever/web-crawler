package com.github.mperever.web.crawler.auth.common;

import com.github.mperever.web.crawler.auth.common.dto.Role;
import com.github.mperever.web.crawler.auth.common.dto.UserInfo;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import static com.github.mperever.web.crawler.auth.common.Jwt.SECRET_KEY_PROPERTY_NAME;

public class JwtTest
{
    private final UserInfo userInfo = new UserInfo( "testUser", Role.ADMIN );
    private final String issuer = UUID.randomUUID().toString();

    @Test
    public void create_jwt_with_secretKey_in_property()
    {
        System.setProperty( SECRET_KEY_PROPERTY_NAME, Jwt.generateSecretKey() );

        final Jwt jwt = new Jwt();
        final String token = jwt.create( userInfo, issuer, null );

        Assert.assertNotNull( token );
    }

    @Test
    public void create_jwt_and_parse_userInfo()
    {
        final Jwt jwt = new Jwt( Jwt.generateSecretKey() );
        final String token = jwt.create( userInfo, issuer, null );
        Assert.assertNotNull( token );

        final UserInfo parsedUserInfo = jwt.parseUserInfo( token );
        Assert.assertNotNull( parsedUserInfo );
        Assert.assertEquals( userInfo.getName(), parsedUserInfo.getName() );
    }
}