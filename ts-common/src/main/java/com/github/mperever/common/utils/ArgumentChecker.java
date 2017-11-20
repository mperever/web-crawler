package com.github.mperever.common.utils;

/**
 * Represents utility class as central place to check arguments
 *
 * @author mperever
 */
public class ArgumentChecker
{
    private ArgumentChecker()
    {
    }

    public static void checkNotEmpty( String argument, String name )
    {
        if ( argument == null || argument.isEmpty() )
        {
            throw new IllegalArgumentException( String.format( "The argument '%s' is not specified.", name ) );
        }
    }

    public static void checkNumberPositive( int argument, String name )
    {
        if ( argument < 0 )
        {
            throw new IllegalArgumentException( String.format( "%s must be positive number.", name ) );
        }
    }

    public static void checkNumberGreaterZero( int argument, String name )
    {
        if ( argument <= 0 )
        {
            throw new IllegalArgumentException( String.format( "%s must be greater than zero.", name ) );
        }
    }

    public static void checkNotNull( Object argument, String name )
    {
        if ( argument == null )
        {
            throw new IllegalArgumentException( String.format( "The argument '%s' is null.", name ) );
        }
    }
}