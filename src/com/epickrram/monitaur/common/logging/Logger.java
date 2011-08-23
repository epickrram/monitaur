package com.epickrram.monitaur.common.logging;

import java.util.logging.Level;

public final class Logger
{
    private final java.util.logging.Logger delegate;

    public Logger(final java.util.logging.Logger delegate)
    {
        this.delegate = delegate;
    }

    public void debug(final String message)
    {
        delegate.log(Level.FINE, message);
    }

    public void info(final String message)
    {
        delegate.log(Level.INFO, message);
    }

    public void warn(final String message)
    {
        delegate.warning(message);
    }

    public void error(final String message, final Throwable throwable)
    {
        delegate.log(Level.SEVERE, message, throwable);
    }

    public static Logger getLogger(final Class cls)
    {
        return new Logger(java.util.logging.Logger.getLogger(cls.getName()));
    }
}