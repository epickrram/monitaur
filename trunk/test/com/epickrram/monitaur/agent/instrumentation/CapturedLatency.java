package com.epickrram.monitaur.agent.instrumentation;

public final class CapturedLatency
{
    private final String className;
    private final String methodId;
    private final long latencyMillis;

    CapturedLatency(final String className, final String methodId, final long latencyMillis)
    {
        this.className = className;
        this.methodId = methodId;
        this.latencyMillis = latencyMillis;
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodId()
    {
        return methodId;
    }

    public long getLatencyMillis()
    {
        return latencyMillis;
    }

    @Override
    public String toString()
    {
        return "CapturedLatency{" +
                "className='" + className + '\'' +
                ", methodId='" + methodId + '\'' +
                ", latencyMillis=" + latencyMillis +
                '}';
    }
}
