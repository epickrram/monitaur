package com.epickrram.monitaur.common;

public interface Agents
{
    void publishAvailableAttributes();
    void monitorNamedAttribute(final String agentId, final String logicalName, final String objectNameRegex, final String attributeNameRegex);
    void monitorNamedCompositeAttribute(final String agentId, final String logicalName, final String objectNameRegex,
                                        final String attributeNameRegex, final String compositeKey);
}
