package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.domain.MonitorType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AbstractCollector<ProviderType> implements Collector<ProviderType>
{
    private final String logicalName;
    private final String hostName;
    private final MonitorType monitorType;

    public AbstractCollector(final String logicalName, final MonitorType monitorType)
    {
        this.logicalName = logicalName;
        this.monitorType = monitorType;
        try
        {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            throw new IllegalStateException("Unable to determine hostname", e);
        }
    }

    @Override
    public String getLogicalName()
    {
        return logicalName;
    }

    @Override
    public String getHostName()
    {
        return hostName;
    }

    @Override
    public MonitorType getMonitorType()
    {
        return monitorType;
    }
}