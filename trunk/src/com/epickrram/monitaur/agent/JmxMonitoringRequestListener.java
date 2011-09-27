package com.epickrram.monitaur.agent;

public interface JmxMonitoringRequestListener
{
    void monitorNamedAttribute(final String logicalName, final String objectNameRegex, final String attributeNameRegex);
    void monitorNamedCompositeAttribute(final String logicalName, final String objectNameRegex,
                                        final String attributeNameRegex, final String compositeKey);
}