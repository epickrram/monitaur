package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.GaugeData;
import com.epickrram.monitaur.common.domain.MonitorType;

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.toNumber;

public final class GaugeCollector extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    private final JmxCollector minimumValueCollector;
    private final JmxCollector currentValueCollector;
    private final JmxCollector maximumValueCollector;

    public GaugeCollector(final String logicalName,
                          final JmxCollector minimumValueCollector,
                          final JmxCollector currentValueCollector,
                          final JmxCollector maximumValueCollector)
    {
        super(logicalName, MonitorType.GAUGE, minimumValueCollector.getType());
        this.minimumValueCollector = minimumValueCollector;
        this.currentValueCollector = currentValueCollector;
        this.maximumValueCollector = maximumValueCollector;
        validate();
    }

    private void validate()
    {
        if(!(minimumValueCollector.getType() == currentValueCollector.getType() &&
                currentValueCollector.getType() == maximumValueCollector.getType()))
        {
            throw new IllegalArgumentException("All gauge value collectors must be the same DataType");
        }
    }

    @Override
    public GaugeData getValue(final MBeanServerConnection provider)
    {
        final Number minimum = toNumber(minimumValueCollector.getValue(provider));
        final Number current = toNumber(currentValueCollector.getValue(provider));
        final Number maximum = toNumber(maximumValueCollector.getValue(provider));
        return new GaugeData(minimum, maximum, current);
    }

    @Override
    public DataType getType()
    {
        return minimumValueCollector.getType();
    }
}