package com.epickrram.monitaur.agent;

public interface JmxMonitoringManager
{
    void startMonitoring(final String objectNameRegex, final String attributeNameRegex);
}