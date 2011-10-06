package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

public final class ServerImpl implements Server
{
    private final MonitorDataStore monitorDataStore;
    private final ServerConfig serverConfig;

    public ServerImpl(final MonitorDataStore monitorDataStore, final ServerConfig serverConfig)
    {
        this.monitorDataStore = monitorDataStore;
        this.serverConfig = serverConfig;
    }

    @Override
    public void reportAvailableAttributes(final AvailableAttributes availableAttributes)
    {
        for (AttributeDetails attributeDetails : availableAttributes.getAttributeDetails())
        {
            serverConfig.addAvailableAgentAttribute(attributeDetails, availableAttributes.getAgentId());
        }
    }

    @Override
    public void receiveMonitorData(final MonitorData monitorData)
    {
        monitorDataStore.onMonitorData(monitorData);
    }
}
