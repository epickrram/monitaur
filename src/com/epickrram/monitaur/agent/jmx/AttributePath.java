package com.epickrram.monitaur.agent.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

public final class AttributePath
{
    private final ObjectName objectName;
    private final MBeanAttributeInfo attributeInfo;

    public AttributePath(final ObjectName objectName, final MBeanAttributeInfo attributeInfo)
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
        return "AttributePath{" +
                "attributeInfo='" + attributeInfo + '\'' +
                ", objectName=" + objectName +
                '}';
    }
}
