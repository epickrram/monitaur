package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.MonitorType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class AbstractCollector<ProviderType> implements Collector<ProviderType>
{
    private final String logicalName;
    private final String hostName;
    private final MonitorType monitorType;
    private final DataType dataType;

    public AbstractCollector(final String logicalName, final MonitorType monitorType, final DataType dataType)
    {
        this.logicalName = logicalName;
        this.monitorType = monitorType;
        this.dataType = dataType;
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

    public DataType getDataType()
    {
        return dataType;
    }
}