package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.domain.GuageData;
import com.epickrram.monitaur.agent.domain.MonitorType;

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.toNumber;

public final class GuageCollector extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    private final JmxCollector minimumValueCollector;
    private final JmxCollector currentValueCollector;
    private final JmxCollector maximumValueCollector;

    public GuageCollector(final String logicalName,
                          final JmxCollector minimumValueCollector,
                          final JmxCollector currentValueCollector,
                          final JmxCollector maximumValueCollector)
    {
        super(logicalName, MonitorType.GUAGE);
        this.minimumValueCollector = minimumValueCollector;
        this.currentValueCollector = currentValueCollector;
        this.maximumValueCollector = maximumValueCollector;
    }

    @Override
    public GuageData getValue(final MBeanServerConnection provider)
    {
        final Number minimum = toNumber(minimumValueCollector.getValue(provider));
        final Number current = toNumber(currentValueCollector.getValue(provider));
        final Number maximum = toNumber(maximumValueCollector.getValue(provider));
        return new GuageData(minimum, maximum, current);
    }
}