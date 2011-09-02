package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.MonitorType;

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.determineNumberDataType;

public final class ConstantNumberCollector<T extends Number> extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    private final T value;
    private final DataType dataType;

    public ConstantNumberCollector(final String logicalName, final T value)
    {
        super(logicalName, MonitorType.SCALAR, ConversionUtil.determineNumberDataType(value));
        this.value = value;
        this.dataType = determineNumberDataType(value);
    }

    @Override
    public T getValue(final MBeanServerConnection provider)
    {
        return value;
    }

    @Override
    public DataType getType()
    {
        return dataType;
    }
}
