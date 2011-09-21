package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.MonitorType;
import com.epickrram.monitaur.common.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.determineDataType;

public final class NamedAttributeJmxCollector extends AbstractJmxCollector implements JmxCollector
{
    private static final Logger LOGGER = Logger.getLogger(NamedAttributeJmxCollector.class);
    
    private final ObjectName objectName;
    private final String attributeName;
    private final String compositeDataKey;

    public NamedAttributeJmxCollector(final String logicalName, final ObjectName objectName,
                                      final MBeanAttributeInfo attributeInfo, final String compositeDataKey)
    {
        super(logicalName, MonitorType.SCALAR, determineDataType(attributeInfo));
        this.objectName = objectName;
        this.attributeName = attributeInfo.getName();
        this.compositeDataKey = compositeDataKey;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Object getValue(final MBeanServerConnection jmxServerConnection)
    {
        try
        {
            final Object attributeValue = jmxServerConnection.getAttribute(objectName, attributeName);
            if(attributeValue instanceof CompositeDataSupport)
            {
                final CompositeDataSupport composite = (CompositeDataSupport) attributeValue;
                if(compositeDataKey != null)
                {
                    return composite.get(compositeDataKey);
                }

                final Set<String> keys = composite.getCompositeType().keySet();
                final Map<String, Object> compositeData = new HashMap<String, Object>();
                for (final String key : keys)
                {
                    compositeData.put(key, composite.get(key));
                }
                return compositeData;
            }
            return attributeValue;
        }
        catch (MBeanException e)
        {
            LOGGER.warn("Failed to query MBean attribute: " + e.getMessage());
        }
        catch (AttributeNotFoundException e)
        {
            LOGGER.warn("Failed to query MBean attribute: " + e.getMessage());
        }
        catch (InstanceNotFoundException e)
        {
            LOGGER.warn("Failed to query MBean attribute: " + e.getMessage());
        }
        catch (ReflectionException e)
        {
            LOGGER.warn("Failed to query MBean attribute: " + e.getMessage());
        }
        catch (IOException e)
        {
            LOGGER.warn("Failed to query MBean attribute: " + e.getMessage());
        }
        return null;
    }
}