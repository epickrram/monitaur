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

import com.epickrram.monitaur.agent.collector.JmxCollector;
import com.epickrram.monitaur.agent.collector.NamedAttributeJmxCollector;
import com.epickrram.monitaur.agent.jmx.JmxAttributeDetails;
import com.epickrram.monitaur.agent.jmx.JmxAttributeFinder;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.logging.Logger;
import com.epickrram.monitaur.common.util.Clock;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class JmxMonitoringAgent implements JmxMonitoringRequestListener
{
    private static final Logger LOGGER = Logger.getLogger(JmxMonitoringAgent.class);
    private static final String AGENT_ID_CONFIG_PARAM = "monitaur.agentId";

    private final Publisher publisher;
    private final MBeanServer platformMBeanServer;
    private final Queue<JmxCollector> collectors = new ConcurrentLinkedQueue<JmxCollector>();
    private final String agentId;

    public JmxMonitoringAgent(final Publisher publisher)
    {
        this.publisher = publisher;
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
            collectors.add(collector);
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
            collectors.add(new NamedAttributeJmxCollector(logicalName, attributeDetails.getObjectName(),
                    attributeDetails.getAttributeInfo(), compositeKey));
        }
    }

    private final class CollectorJob implements Runnable
    {
        @Override
        public void run()
        {
            LOGGER.info("Polling collectors");
            for (JmxCollector collector : collectors)
            {
                try
                {
                    final MonitorData value =
                            new MonitorData(collector.getMonitorType(), collector.getLogicalName(),
                                    agentId, collector.getValue(platformMBeanServer), Clock.getCurrentMillis());
                    LOGGER.info("Retrieved data: " + value);
                    publisher.publish(value);
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
}