package com.github.mperever.web.crawler.auth.common;

import com.github.mperever.web.crawler.auth.common.dto.Role;
import com.github.mperever.web.crawler.auth.common.dto.UserPrincipal;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import static com.github.mperever.web.crawler.auth.common.Jwt.SECRET_KEY_PROPERTY_NAME;

public class JwtTest
{
    private final UserPrincipal userPrincipal = new UserPrincipal( "testUser", Role.ADMIN );
    private final String issuer = UUID.randomUUID().toString();

    @Test
    public void create_jwt_with_secretKey_in_property()
    {
        System.setProperty( SECRET_KEY_PROPERTY_NAME, Jwt.generateSecretKey() );

        final Jwt jwt = new Jwt();
        final String token = jwt.create( userPrincipal, issuer, null );

        Assert.assertNotNull( token );
    }

    @Test
    public void create_jwt_and_parse_userInfo()
    {
        final Jwt jwt = new Jwt( Jwt.generateSecretKey() );
        final String token = jwt.create( userPrincipal, issuer, null );
        Assert.assertNotNull( token );

        final UserPrincipal parsedUserPrincipal = jwt.parseUserPrincipal( token );
        Assert.assertNotNull( parsedUserPrincipal );
        Assert.assertEquals( userPrincipal.getName(), parsedUserPrincipal.getName() );
    }
}