package com.github.mperever.web.crawler.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents class to validate arguments with primitive types
 *
 * @author mperever
 */
public class ArgumentsValidator
{
    private final Set<String> errors = new HashSet<>();

    public ArgumentsValidator notEmpty( String argument, String name )
    {
        if ( argument == null || argument.isEmpty() )
        {
            errors.add( String.format( "The argument '%s' is not specified.", name ) );
        }

        return this;
    }

    public ArgumentsValidator numberNotNegative( int argument, String name )
    {
        if ( argument < 0 )
        {
            errors.add( String.format( "'%s' must not be negative number.", name ) );
        }

        return this;
    }

    public ArgumentsValidator numberPositive( int argument, String name )
    {
        if ( argument <= 0 )
        {
            errors.add( String.format( "'%s' must be positive number (greater than zero).", name ) );
        }

        return this;
    }

    public ArgumentsValidator notNull( Object argument, String name )
    {
        if ( argument == null )
        {
            errors.add( String.format( "The argument '%s' is null.", name ) );
        }

        return this;
    }

    public String[] validate()
    {
        final String[] result = errors.toArray( new String[ errors.size() ] );
        errors.clear();

        return result;
    }
}