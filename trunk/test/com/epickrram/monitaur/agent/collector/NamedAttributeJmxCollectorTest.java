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
package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.jmx.JmxAttributeDetails;
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