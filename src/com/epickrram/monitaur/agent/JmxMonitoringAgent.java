package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.collector.JmxCollector;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.util.Clock;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class JmxMonitoringAgent
{
    private final Publisher publisher;
    private final Queue<JmxCollector> collectors =
            new ConcurrentLinkedQueue<JmxCollector>();
    private final MBeanServer platformMBeanServer;

    public JmxMonitoringAgent(final Publisher publisher)
    {
        this.publisher = publisher;
        platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    public void start(final ScheduledExecutorService scheduledExecutorService)
    {
        scheduledExecutorService.scheduleAtFixedRate(new CollectorJob(), 0L, 1L, TimeUnit.SECONDS);
    }

    public void setCollectors(final Collection<JmxCollector> collector)
    {
        collectors.clear();
        collectors.addAll(collector);
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