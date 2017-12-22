package com.github.mperever.web.crawler.auth.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils
{
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String SALT_RESOURCE_NAME = "salt";
    private static final String SALT = getResourceContent( SALT_RESOURCE_NAME );

    private DigestUtils()
    {
    }

    private static String getResourceContent( final String resourceName )
    {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final int defaultBufferSize = 1024;
        byte[] buffer = new byte[ defaultBufferSize ];
        int length;
        try ( final InputStream resourceStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream( resourceName ) )
        {
            while ( ( length = resourceStream.read( buffer ) ) != -1 )
            {
                result.write( buffer, 0, length );
            }

            return result.toString( DEFAULT_CHARSET.name() );
        }
        catch ( IOException ex )
        {
            throw new IllegalArgumentException( "Could not find resource with name: " + resourceName, ex );
        }
    }

    public static String hash( final String userName, final String password ) throws NoSuchAlgorithmException
    {
        final String source = userName + password;
        return sha512Hex( source, SALT );
    }

    private static String sha512Hex( final String source, final String salt ) throws NoSuchAlgorithmException
    {
        final MessageDigest md = MessageDigest.getInstance( "SHA-512" );
        md.update( salt.getBytes( DEFAULT_CHARSET ) );

        final byte[] hash = md.digest( source.getBytes( DEFAULT_CHARSET ) );

        return bytesToHex( hash );
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Lightweight and fast way to convert bytes to hex without extra dependencies.
     *
     * @param bytes The source bytes to convert
     * @return hex presentation for bytes
     */
    private static String bytesToHex( byte[] bytes )
    {
        final char[] hexChars = new char[ bytes.length * 2 ];
        for ( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }
}