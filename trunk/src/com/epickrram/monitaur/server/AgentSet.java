package com.epickrram.monitaur.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AgentSet
{
    private final List<String> agentIds = new CopyOnWriteArrayList<String>();
    private final String logicalName;
    private final boolean includeAllAgents;

    public AgentSet(final String logicalName, final boolean includeAllAgents)
    {
        this.logicalName = logicalName;
        this.includeAllAgents = includeAllAgents;
    }

    public void addAgentId(final String agentId)
    {
        agentIds.add(agentId);
    }

    public List<String> getAgentIds()
    {
        return agentIds;
    }

    public String getLogicalName()
    {
        return logicalName;
    }

    public boolean isIncludeAllAgents()
    {
        return includeAllAgents;
    }
}
