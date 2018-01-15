package com.github.mperever.web.crawler.auth.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mperever.web.crawler.auth.common.dto.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents facade to create and parse signed Java Web token as part of authorisation process.
 *
 * @author mperever
 */
public class Jwt
{
    private static final Logger logger = LoggerFactory.getLogger( Jwt.class );

    static final String SECRET_KEY_PROPERTY_NAME = "secretKey";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String USER_CLAIM_NAME = "user";

    private final String secretKey;
    private final JwtParser tokenParser;

    private final ObjectMapper jacksonMapper = new ObjectMapper();

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

    public static String generateSecretKey()
    {
        final Key secret = MacProvider.generateKey( SIGNATURE_ALGORITHM );
        final byte[] secretBytes = secret.getEncoded();
        return Base64.getEncoder().encodeToString( secretBytes );
    }

    /**
     * Creates signed java web token for specified user principal.
     *
     * @param user The user principal as a part of token
     * @param issuer The value of token issuer
     * @param expirationDate The date when token considered as expired
     * @return java web token
     */
    public String create( UserPrincipal user, String issuer, Date expirationDate )
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
                .signWith( SIGNATURE_ALGORITHM, secretKey )
                .compact();
    }

    /**
     * Parses token to get {@link UserPrincipal}.
     *
     * @param token The token to parse
     * @return user principal object or returns null if the token is invalid.
     */
    public UserPrincipal parseUserPrincipal( String token )
    {
        try
        {
            final Claims bodyClaims = tokenParser.parseClaimsJws( token ).getBody();
            logger.debug( bodyClaims.toString() );

            // workaround to get UserPrincipal object from JWT body claim.
            final Object userMap = bodyClaims.get( USER_CLAIM_NAME );
            if ( userMap != null && userMap instanceof LinkedHashMap )
            {
                return jacksonMapper.convertValue( userMap, UserPrincipal.class );
            }

        } catch ( Exception ex )
        {
            logger.error( ex.getMessage(), ex );
        }

        return null;
    }
}