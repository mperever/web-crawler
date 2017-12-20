package com.github.mperever.web.crawler.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.github.mperever.web.crawler.common.json.JacksonJsonSerializer;
import com.github.mperever.web.crawler.common.json.JsonSerializer;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jwt
{
    private static final Logger logger = LoggerFactory.getLogger( Jwt.class );

    private static final String SECRET_KEY_PROPERTY_NAME = "secretKey";
    private static final String USER_CLAIM_NAME = "user";

    private final String secretKey;
    private final JwtParser tokenParser;
    private final JsonSerializer serializer = new JacksonJsonSerializer();

    public Jwt( String secretKey )
    {
        this.secretKey = secretKey;
        tokenParser = Jwts.parser().setSigningKey( secretKey );
    }

    public Jwt()
    {
        this( readSecretKey() );
    }

    private static String readSecretKey()
    {
        final String secretKey = System.getProperty( SECRET_KEY_PROPERTY_NAME );
        if ( secretKey == null || secretKey.isEmpty() )
        {
            throw new IllegalStateException( String.format( "System property '%s' is not set", SECRET_KEY_PROPERTY_NAME ) );
        }
        return secretKey;
    }

    public String create( UserInfo user, String issuer, Date expirationDate )
    {
        final JwtBuilder tokenBuilder = Jwts.builder()
                .setId( UUID.randomUUID().toString() )
                .setIssuer( issuer )
                .claim( USER_CLAIM_NAME, user );

        if ( expirationDate != null )
        {
            tokenBuilder
                    .setExpiration( expirationDate )
                    .setIssuedAt( new Date() );
        }
        return tokenBuilder
                .signWith( SignatureAlgorithm.HS256, secretKey )
                .compact();
    }

    // TODO: JavaDoc. Returns null if the token is invalid or {@link UserInfo} can not be parsed from the token.
    public UserInfo parseUserInfo( String token )
    {
        try
        {
            final Claims bodyClaims = tokenParser.parseClaimsJws( token ).getBody();
            logger.debug( bodyClaims.toString() );

            // workaround to get UserInfo object from JWT body claim.
            final Serializable userMap = ( Serializable )bodyClaims.get( USER_CLAIM_NAME );
            if ( userMap != null )
            {
                return serializer.decode( serializer.encode( userMap ), UserInfo.class );
            }

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
        }

        return null;
    }
}