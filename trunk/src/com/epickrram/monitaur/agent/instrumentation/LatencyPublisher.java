package com.epickrram.monitaur.agent.instrumentation;

public interface LatencyPublisher
{
    void onCapturedLatency(final String className, final String methodId, final long latencyMillis);
}