package com.epickrram.monitaur.agent.collector;

import javax.management.MBeanServerConnection;
import com.epickrram.monitaur.common.domain.*;

public interface JmxCollector extends Collector<MBeanServerConnection>
{
    Object getValue(final MBeanServerConnection jmxServerConnection);
    DataType getType();
}