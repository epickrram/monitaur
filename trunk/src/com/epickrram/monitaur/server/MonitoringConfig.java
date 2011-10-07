package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.util.Set;

public final class MonitoringConfig implements Comparable<MonitoringConfig>
{
    private final AttributeDetails attributeDetails;
    private final Set<AgentState> agentStates;
    private final boolean allHosts;

    public MonitoringConfig(final AttributeDetails attributeDetails, final Set<AgentState> agentStates, final boolean allHosts)
    {
        this.attributeDetails = attributeDetails;
        this.agentStates = agentStates;
        this.allHosts = allHosts;
    }

    public AttributeDetails getAttributeDetails()
    {
        return attributeDetails;
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
        int comparison = attributeDetails.getObjectName().compareTo(other.attributeDetails.getObjectName());
        if(comparison == 0)
        {
            comparison = attributeDetails.getAttributeName().compareTo(other.getAttributeDetails().getAttributeName());
        }

        return comparison;
    }
}
