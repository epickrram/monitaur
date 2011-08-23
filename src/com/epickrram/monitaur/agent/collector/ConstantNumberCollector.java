package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.domain.MonitorType;

import javax.management.MBeanServerConnection;

public final class ConstantNumberCollector extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    private final Number value;

    public ConstantNumberCollector(final String logicalName, final Number value)
    {
        super(logicalName, MonitorType.SCALAR);
        this.value = value;
    }

    @Override
    public Number getValue(final MBeanServerConnection provider)
    {
        return value;
    }
}
