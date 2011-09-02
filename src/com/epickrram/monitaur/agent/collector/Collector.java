package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.MonitorType;

public interface Collector<ProviderType>
{
    String getLogicalName();
    String getHostName();
    MonitorType getMonitorType();
    Object getValue(final ProviderType providerType);
}
