package com.epickrram.monitaur.common;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.JmxAttributeDetails;

import java.util.Collection;

public interface Server
{
    void reportAvailableAttributes(final AvailableAttributes availableAttributes);
    void receiveMonitorData(final MonitorData monitorData);
}
