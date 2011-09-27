package com.epickrram.monitaur.agent.jmx;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import java.lang.management.ManagementFactory;
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
