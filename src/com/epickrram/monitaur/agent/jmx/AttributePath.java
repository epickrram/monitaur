package com.epickrram.monitaur.agent.jmx;

import javax.management.ObjectName;

public final class AttributePath
{
    private final ObjectName objectName;
    private final String attributeName;

    public AttributePath(final ObjectName objectName, final String attributeName)
    {
        this.objectName = objectName;
        this.attributeName = attributeName;
    }

    public ObjectName getObjectName()
    {
        return objectName;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    @Override
    public String toString()
    {
        return "AttributePath{" +
                "attributeName='" + attributeName + '\'' +
                ", objectName=" + objectName +
                '}';
    }
}
