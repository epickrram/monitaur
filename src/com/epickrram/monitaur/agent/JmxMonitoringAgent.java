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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class JmxMonitoringAgent implements JmxMonitoringRequestListener
{
    private static final Logger LOGGER = Logger.getLogger(JmxMonitoringAgent.class);
    
    private final Publisher publisher;
    private final MBeanServer platformMBeanServer;
    private final Queue<JmxCollector> collectors = new ConcurrentLinkedQueue<JmxCollector>();

    public JmxMonitoringAgent(final Publisher publisher)
    {
        this.publisher = publisher;
        new JmxMonitoringManagerImpl(this).registerSelf();
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    public void start(final ScheduledExecutorService scheduledExecutorService)
    {
        scheduledExecutorService.scheduleAtFixedRate(new CollectorJob(), 0L, 1L, TimeUnit.SECONDS);
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
            for (JmxCollector collector : collectors)
            {
                try
                {
                    final MonitorData value =
                            new MonitorData(collector.getMonitorType(), collector.getLogicalName(),
                                    collector.getHostName(), collector.getValue(platformMBeanServer), Clock.getCurrentMillis());
                    publisher.publish(value);
                }
                catch(Exception e)
                {
                    // ignore
                }
            }
        }
    }
}