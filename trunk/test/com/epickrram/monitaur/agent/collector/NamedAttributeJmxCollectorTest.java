package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.agent.jmx.JmxAttributeDetails;
import com.epickrram.monitaur.agent.jmx.JmxAttributeFinder;
import org.junit.Test;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public final class NamedAttributeJmxCollectorTest
{
    private static final String LOGICAL_NAME = "logicalName";

    @Test
    public void shouldRetrieveSimpleValue() throws Exception
    {
        final JmxAttributeDetails startTimeDetails = findJmxAttribute("java\\.lang:type=Runtime", "^StartTime$");
        final NamedAttributeJmxCollector collector =
                new NamedAttributeJmxCollector(LOGICAL_NAME, startTimeDetails.getObjectName(),
                        startTimeDetails.getAttributeInfo(), null);

        assertThat((Long) collector.getValue(getPlatformMBeanServer()), lessThan(System.currentTimeMillis()));
    }

    @Test
    public void shouldRetrieveCompositeValue() throws Exception
    {
        final JmxAttributeDetails maxHeapUsageDetails = findJmxAttribute("java\\.lang:type=Memory", "^HeapMemoryUsage$");
        final NamedAttributeJmxCollector collector =
                new NamedAttributeJmxCollector(LOGICAL_NAME, maxHeapUsageDetails.getObjectName(),
                        maxHeapUsageDetails.getAttributeInfo(), "max");

        assertThat((Long) collector.getValue(getPlatformMBeanServer()), greaterThan(0L));
    }

    private JmxAttributeDetails findJmxAttribute(final String mbeanNameRegex, final String attributeRegex)
    {
        return new JmxAttributeFinder(mbeanNameRegex, attributeRegex).findAttribute();
    }
}