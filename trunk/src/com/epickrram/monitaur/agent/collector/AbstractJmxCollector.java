package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.MonitorType;

import javax.management.MBeanServerConnection;

public abstract class AbstractJmxCollector extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    public AbstractJmxCollector(final String logicalName, final MonitorType monitorType, final DataType dataType)
    {
        super(logicalName, monitorType, dataType);
    }

    @Override
    public DataType getType()
    {
        return super.getDataType();
    }
}
