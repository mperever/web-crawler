package com.github.mperever.web.crawler.common.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils
{
    private DigestUtils()
    {
        // TODO: get salt from resources
    }

    public static String hash( final String userName, final String password ) throws NoSuchAlgorithmException
    {
        final String source = userName + password;
        // TODO: Get salt from somewhere. Resources ?
        return sha512Hex( source, "XXXX" );
    }

    // TODO: use this method calculate has for user and password
    private static String sha512Hex( final String source, final String salt ) throws NoSuchAlgorithmException
    {
        final MessageDigest md = MessageDigest.getInstance( "SHA-512" );
        md.update( salt.getBytes( StandardCharsets.UTF_8 ) );

        final byte[] hash = md.digest( source.getBytes( StandardCharsets.UTF_8 ) );

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
        return new String(hexChars);
    }
}