package com.epickrram.monitaur.agent.domain;

public final class MonitorData<T>
{
    private final MonitorType monitorType;
    private final String logicalName;
    private final String host;
    private final T datum;
    private final long timestamp;

    public MonitorData(final MonitorType monitorType, final String logicalName, final String host, final T datum, final long timestamp)
    {
        this.logicalName = logicalName;
        this.datum = datum;
        this.timestamp = timestamp;
        this.monitorType = monitorType;
        this.host = host;
    }

    public MonitorType getMonitorType()
    {
        return monitorType;
    }

    public String getLogicalName()
    {
        return logicalName;
    }

    public String getHost()
    {
        return host;
    }

    public T getDatum()
    {
        return datum;
    }

    public String getDatumAsString()
    {
        return String.valueOf(datum);
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}