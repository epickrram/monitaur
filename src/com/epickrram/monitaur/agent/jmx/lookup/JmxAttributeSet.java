package com.epickrram.monitaur.agent.jmx.lookup;

import com.epickrram.monitaur.agent.jmx.AttributePath;

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

public final class JmxAttributeSet
{
    private final Collection<JmxSearchTerm> searchTerms = new ArrayList<JmxSearchTerm>();

    public Collection<AttributePath> getMonitoredAttributes()
    {
        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        final Set<ObjectName> objectNames = platformMBeanServer.queryNames(null, null);
        final Collection<AttributePath> monitoredAttributes = new ArrayList<AttributePath>();
        try
        {
            for (ObjectName objectName : objectNames)
            {
                final MBeanInfo mBeanInfo = platformMBeanServer.getMBeanInfo(objectName);
                final MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
                for (MBeanAttributeInfo attribute : attributes)
                {
                    for (JmxSearchTerm searchTerm : searchTerms)
                    {
                        if(searchTerm.matches(objectName, attribute))
                        {
                            monitoredAttributes.add(new AttributePath(objectName, attribute));
                        }
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
        return monitoredAttributes;
    }

    public void addJmxSearchTerm(final JmxSearchTerm jmxSearchTerm)
    {
        searchTerms.add(jmxSearchTerm);
    }

    public void addJmxSearchTerm(final String objectNameRegex, final String attributeNameRegex)
    {
        searchTerms.add(new JmxSearchTerm(objectNameRegex, attributeNameRegex));
    }
}