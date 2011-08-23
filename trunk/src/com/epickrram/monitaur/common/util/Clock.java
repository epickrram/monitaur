package com.epickrram.monitaur.common.util;

public final class Clock
{
    private static TimeProvider timeProvider = new SystemTimeProvider();

    public static long getCurrentMillis()
    {
        return timeProvider.currentMillis();
    }

    private static class SystemTimeProvider implements TimeProvider
    {
        @Override
        public long currentMillis()
        {
            return System.currentTimeMillis();
        }
    }
}
