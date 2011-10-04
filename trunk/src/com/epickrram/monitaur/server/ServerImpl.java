package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.JmxAttributeDetails;

import java.util.Collection;

public final class ServerImpl implements Server
{
    private final MonitorDataStore monitorDataStore;

    public ServerImpl(final MonitorDataStore monitorDataStore)
    {
        this.monitorDataStore = monitorDataStore;
    }

    @Override
    public void reportAvailableAttributes(final String agentId, final Collection<JmxAttributeDetails> availableAttributes)
    {
    }

    @Override
    public void receiveMonitorData(final MonitorData monitorData)
    {
        monitorDataStore.onMonitorData(monitorData);
    }
}
