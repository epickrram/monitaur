/*
Copyright 2011 Mark Price

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.epickrram.monitaur.agent;

import com.epickrram.freewheel.util.Logger;
import com.epickrram.monitaur.agent.collector.JmxCollector;
import com.epickrram.monitaur.agent.collector.NamedAttributeJmxCollector;
import com.epickrram.monitaur.agent.jmx.JmxAttributeFinder;
import com.epickrram.monitaur.common.Agents;
import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.AttributeDetails;
import com.epickrram.monitaur.common.jmx.JmxAttributeDetails;
import com.epickrram.monitaur.common.util.Clock;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.openmbean.CompositeType;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class JmxMonitoringAgent implements JmxMonitoringRequestListener, Agents
{
    private static final Logger LOGGER = Logger.getLogger(JmxMonitoringAgent.class);
    private static final String AGENT_ID_CONFIG_PARAM = "monitaur.agentId";

    private final Server server;
    private final MBeanServer platformMBeanServer;
    private final Map<String, JmxCollector> collectors = new ConcurrentHashMap<String, JmxCollector>();
    private final String agentId;

    public JmxMonitoringAgent(final Server server)
    {
        this.server = server;
        new JmxMonitoringManagerImpl(this).registerSelf();
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        agentId = getAgentId();
    }

    public void start(final ScheduledExecutorService scheduledExecutorService)
    {
        LOGGER.info("Scheduling poll job for 3 second interval");
        scheduledExecutorService.scheduleAtFixedRate(new CollectorJob(), 0L, 3L, TimeUnit.SECONDS);
    }

    @Override
    public void monitorNamedAttribute(final String logicalName, final String objectNameRegex, final String attributeNameRegex)
    {
        LOGGER.info("Request to monitor attribute defined by: " + objectNameRegex + "." + attributeNameRegex);
        final JmxAttributeDetails attributeDetails = new JmxAttributeFinder(objectNameRegex, attributeNameRegex).findAttribute();
        if(attributeDetails != null)
        {
            final NamedAttributeJmxCollector collector =
                    new NamedAttributeJmxCollector(logicalName, attributeDetails.getObjectName(), attributeDetails.getAttributeInfo());

            LOGGER.info("Adding collector: " + collector);
            collectors.put(logicalName, collector);
        }
        else
        {
            LOGGER.info("Could not find attribute");
        }
    }

    @Override
    public void monitorNamedCompositeAttribute(final String logicalName, final String objectNameRegex,
                                               final String attributeNameRegex, final String compositeKey)
    {
        final JmxAttributeDetails attributeDetails = new JmxAttributeFinder(objectNameRegex, attributeNameRegex).findAttribute();
        if(attributeDetails != null)
        {
            collectors.put(logicalName, new NamedAttributeJmxCollector(logicalName, attributeDetails.getObjectName(),
                    attributeDetails.getAttributeInfo(), compositeKey));
        }
    }

    @Override
    public void publishAvailableAttributes()
    {
        final List<JmxAttributeDetails> availableAttributes = new ArrayList<JmxAttributeDetails>(new JmxAttributeFinder("", "").listAttributes());
        server.reportAvailableAttributes(new AvailableAttributes(agentId, toAttributeDetails(availableAttributes)));
    }

    @Override
    public void monitorNamedAttribute(final String agentId, final String logicalName, final String objectNameRegex, final String attributeNameRegex)
    {
        if(this.agentId.equals(agentId))
        {
            monitorNamedAttribute(logicalName, objectNameRegex, attributeNameRegex);
        }
    }

    @Override
    public void monitorNamedCompositeAttribute(final String agentId, final String logicalName, final String objectNameRegex, final String attributeNameRegex, final String compositeKey)
    {
        if(this.agentId.equals(agentId))
        {
            monitorNamedCompositeAttribute(logicalName, objectNameRegex, attributeNameRegex, compositeKey);
        }
    }

    private final class CollectorJob implements Runnable
    {
        @Override
        public void run()
        {
            for (JmxCollector collector : collectors.values())
            {
                try
                {
                    final MonitorData value =
                            new MonitorData(collector.getMonitorType(), collector.getLogicalName(),
                                    agentId, collector.getValue(platformMBeanServer), Clock.getCurrentMillis());
                    server.receiveMonitorData(value);
                }
                catch(Throwable e)
                {
                    LOGGER.error("Failed to poll collector " + collector, e);
                }
            }
        }
    }

    private String getAgentId()
    {
        return System.getProperty(AGENT_ID_CONFIG_PARAM) != null ?
                System.getProperty(AGENT_ID_CONFIG_PARAM) :
                getLocalHostName();
    }

    private String getLocalHostName()
    {
        try
        {
            return Inet4Address.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            throw new IllegalStateException("Cannot determine local hostname", e);
        }
    }

    private List<AttributeDetails> toAttributeDetails(final List<JmxAttributeDetails> availableAttributes)
    {
        final List<AttributeDetails> detailList = new ArrayList<AttributeDetails>(availableAttributes.size());
        for (JmxAttributeDetails availableAttribute : availableAttributes)
        {
            final MBeanAttributeInfo attributeInfo = availableAttribute.getAttributeInfo();
            if(attributeInfo.getType().equals("javax.management.openmbean.CompositeData"))
            {
                final Set<String> compositeNames = ((CompositeType) attributeInfo.
                        getDescriptor().getFieldValues("openType")[0]).keySet();
                for (String compositeName : compositeNames)
                {
                    detailList.add(new AttributeDetails(availableAttribute.getObjectName().toString(),
                            attributeInfo.getName(), compositeName));
                }
                
            }
            else
            {
                detailList.add(new AttributeDetails(availableAttribute.getObjectName().toString(),
                        attributeInfo.getName()));
            }
        }
        return detailList;
    }
}