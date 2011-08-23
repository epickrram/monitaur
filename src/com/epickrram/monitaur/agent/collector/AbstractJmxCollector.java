package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.domain.MonitorType;

import javax.management.MBeanServerConnection;

public abstract class AbstractJmxCollector extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    public AbstractJmxCollector(final String logicalName, final MonitorType monitorType)
    {
        super(logicalName, monitorType);
    }
}
