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
package com.epickrram.monitaur.agent.jmx;

import com.epickrram.monitaur.common.jmx.JmxAttributeDetails;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

public final class JmxAttributeFinder
{
    private final Pattern mbeanNameRegex;
    private final Pattern attributeRegex;

    public JmxAttributeFinder(final String mbeanNameRegex, final String attributeRegex)
    {
        if(mbeanNameRegex == null && attributeRegex == null)
        {
            throw new IllegalArgumentException("Specify at least one of mbeanNameRegex and attributeRegex");
        }
        this.mbeanNameRegex = mbeanNameRegex == null ? null : Pattern.compile(mbeanNameRegex);
        this.attributeRegex = attributeRegex == null ? null : Pattern.compile(attributeRegex);
    }

    public Collection<JmxAttributeDetails> listAttributes()
    {
        final Collection<JmxAttributeDetails> attributeList = new ArrayList<JmxAttributeDetails>();
        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        final Set<ObjectName> objectNames = platformMBeanServer.queryNames(null, null);
        try
        {
            for (ObjectName objectName : objectNames)
            {
                final MBeanInfo mBeanInfo = platformMBeanServer.getMBeanInfo(objectName);
                final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                for (MBeanAttributeInfo attribute : attributes)
                {
                    if(matches(objectName, attribute))
                    {
                        attributeList.add(new JmxAttributeDetails(objectName, attribute));
                    }
                }
            }
        }
        catch (InstanceNotFoundException e)
        {
             throw new RuntimeException("Unable to query MBeans", e);
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException("Unable to query MBeans", e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException("Unable to query MBeans", e);
        }
        return attributeList;
    }

    public JmxAttributeDetails findAttribute()
    {
        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        final Set<ObjectName> objectNames = platformMBeanServer.queryNames(null, null);
        try
        {
            for (ObjectName objectName : objectNames)
            {
                final MBeanInfo mBeanInfo = platformMBeanServer.getMBeanInfo(objectName);
                final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                for (MBeanAttributeInfo attribute : attributes)
                {
                    if(matches(objectName, attribute))
                    {
                        return new JmxAttributeDetails(objectName, attribute);
                    }
                }
            }
        }
        catch (InstanceNotFoundException e)
        {
             throw new RuntimeException("Unable to query MBeans", e);
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException("Unable to query MBeans", e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException("Unable to query MBeans", e);
        }
        return null;
    }

    @Override
    public String toString()
    {
        return "JmxAttributeFinder{" +
                "mbeanNameRegex=" + mbeanNameRegex.pattern() +
                ", attributeRegex=" + attributeRegex.pattern() +
                '}';
    }

    private boolean matches(final ObjectName objectName, final MBeanAttributeInfo attribute)
    {
        final boolean objectNameMatches = mbeanNameRegex == null || mbeanNameRegex.matcher(objectName.getCanonicalName()).find();
        final boolean attributeNameMatches = attributeRegex == null || attributeRegex.matcher(attribute.getName()).find();
        return objectNameMatches && attributeNameMatches;
    }
}
