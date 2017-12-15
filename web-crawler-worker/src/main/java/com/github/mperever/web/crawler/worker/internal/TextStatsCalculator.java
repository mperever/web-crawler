package com.github.mperever.web.crawler.worker.internal;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents calculator for text word statistics (pure function).
 *
 * @author mperever
 */
public class TextStatsCalculator
{
    private TextStatsCalculator()
    {
    }

    /**
     * Calculates text word statistics.
     *
     * @param text The text for calculation
     * @return Word statistics.
     */
    public static Map<String, Long> calculateWordStatistic( String text )
    {
        return Arrays.stream( text.split("[\\p{Punct}\\s\\d]+" ) )
                .map( word -> word.toLowerCase( Locale.ENGLISH ).trim() )
                .filter( word -> !word.isEmpty() && word.length() != 1 )
                .collect( Collectors.groupingBy( word -> word, Collectors.counting() ) );
    }
}