package com.epickrram.monitaur.server;

import com.epickrram.freewheel.util.Logger;
import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

public final class ServerImpl implements Server
{
    private static final Logger LOGGER = Logger.getLogger(ServerImpl.class);
    
    private final MonitorDataStore monitorDataStore;

    public ServerImpl(final MonitorDataStore monitorDataStore)
    {
        this.monitorDataStore = monitorDataStore;
    }

    @Override
    public void reportAvailableAttributes(final AvailableAttributes availableAttributes)
    {
        LOGGER.info("Received available attributes from agent: " + availableAttributes.getAgentId());
        for (AttributeDetails attributeDetails : availableAttributes.getAttributeDetails())
        {
            LOGGER.info(attributeDetails.getObjectName() + " -> " + attributeDetails.getAttributeName());
        }
    }

    @Override
    public void receiveMonitorData(final MonitorData monitorData)
    {
        monitorDataStore.onMonitorData(monitorData);
    }
}
