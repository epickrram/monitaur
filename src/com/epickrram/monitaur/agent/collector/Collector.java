package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.domain.MonitorType;

public interface Collector<ProviderType>
{
    String getLogicalName();
    String getHostName();
    MonitorType getMonitorType();
    Object getValue(final ProviderType providerType);
}
