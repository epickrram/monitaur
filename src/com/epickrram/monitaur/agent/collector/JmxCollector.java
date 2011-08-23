package com.epickrram.monitaur.agent.collector;

import javax.management.MBeanServerConnection;

public interface JmxCollector extends Collector<MBeanServerConnection>
{
    Object getValue(final MBeanServerConnection jmxServerConnection);
}