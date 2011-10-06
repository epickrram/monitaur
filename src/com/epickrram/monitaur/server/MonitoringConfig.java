package com.epickrram.monitaur.server;

import java.util.Set;

public final class MonitoringConfig implements Comparable<MonitoringConfig>
{
    private final String objectName;
    private final String attributeName;
    private final Set<AgentState> agentStates;
    private final boolean allHosts;

    public MonitoringConfig(final String objectName, final String attributeName,
                            final Set<AgentState> agentStates, final boolean allHosts)
    {
        this.objectName = objectName;
        this.attributeName = attributeName;
        this.agentStates = agentStates;
        this.allHosts = allHosts;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public Set<AgentState> getAgentStates()
    {
        return agentStates;
    }

    public boolean isAllHosts()
    {
        return allHosts;
    }

    @Override
    public int compareTo(final MonitoringConfig other)
    {
        int comparison = objectName.compareTo(other.objectName);
        if(comparison == 0)
        {
            comparison = attributeName.compareTo(other.attributeName);
        }

        return comparison;
    }
}
