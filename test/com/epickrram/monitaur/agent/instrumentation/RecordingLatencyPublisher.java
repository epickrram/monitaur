package com.epickrram.monitaur.agent.instrumentation;

import java.util.ArrayList;
import java.util.List;

public final class RecordingLatencyPublisher implements LatencyPublisher
{
    private final List<CapturedLatency> capturedLatencyList = new ArrayList<CapturedLatency>();

    @Override
    public void onCapturedLatency(final String className, final String methodId, final long latencyMillis)
    {
        capturedLatencyList.add(new CapturedLatency(className, methodId, latencyMillis));
    }

    public List<CapturedLatency> getCapturedLatencyList()
    {
        return capturedLatencyList;
    }
}
