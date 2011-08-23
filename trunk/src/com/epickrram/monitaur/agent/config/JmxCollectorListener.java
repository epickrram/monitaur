package com.epickrram.monitaur.agent.config;

import com.epickrram.monitaur.agent.collector.JmxCollector;

interface JmxCollectorListener
{
    void receiveCollector(final String nodeName, final JmxCollector collector);
}
