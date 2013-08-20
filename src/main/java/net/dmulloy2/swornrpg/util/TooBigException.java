package net.dmulloy2.swornrpg.util;

/**
 * @author dmulloy2
 */

public class TooBigException extends RuntimeException 
{
    private static final long serialVersionUID = 1L;

    public TooBigException(final String message) 
    {
        super(message);
    }
}