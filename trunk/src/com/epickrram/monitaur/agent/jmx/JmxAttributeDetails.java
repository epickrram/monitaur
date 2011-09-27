package com.epickrram.monitaur.agent.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

public final class JmxAttributeDetails
{
    private final ObjectName objectName;
    private final MBeanAttributeInfo attributeInfo;

    JmxAttributeDetails(final ObjectName objectName, final MBeanAttributeInfo attributeInfo)
    {
        this.objectName = objectName;
        this.attributeInfo = attributeInfo;
    }

    public ObjectName getObjectName()
    {
        return objectName;
    }

    public MBeanAttributeInfo getAttributeInfo()
    {
        return attributeInfo;
    }

    @Override
    public String toString()
    {
        return "JmxAttributeDetails{" +
                "attributeInfo='" + attributeInfo + '\'' +
                ", objectName=" + objectName +
                '}';
    }
}
