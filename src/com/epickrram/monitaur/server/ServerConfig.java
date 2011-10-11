package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.Agents;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class ServerConfig
{
    private final ConcurrentMap<String, MonitoringGroup> monitoringGroupsByName =
            new ConcurrentHashMap<String, MonitoringGroup>();

    private final ConcurrentMap<AttributeDetails, Set<AgentState>> agentIdListByAttribute =
            new ConcurrentHashMap<AttributeDetails, Set<AgentState>>();

    private volatile Agents agents;

    public ServerConfig(final Agents agents)
    {
        this.agents = agents;
    }

    public void addAvailableAgentAttribute(final AttributeDetails attributeDetails, final String agentId)
    {
        Set<AgentState> agentIdList = agentIdListByAttribute.get(attributeDetails);
        if(agentIdList == null)
        {
            agentIdList = new CopyOnWriteArraySet<AgentState>();
            final Set<AgentState> existing = agentIdListByAttribute.putIfAbsent(attributeDetails, agentIdList);
            if(existing != null)
            {
                agentIdList = existing;
            }
        }

        final AgentState newAgentState = new AgentState(agentId, false);
        if(!agentIdList.contains(newAgentState))
        {
            agentIdList.add(newAgentState);
        }
    }

    public ConcurrentMap<AttributeDetails, Set<AgentState>> getAgentIdListByAttribute()
    {
        return agentIdListByAttribute;
    }

    public void updateAgentIdListByAttribute(final MonitoringConfig[] update)
    {
        for (MonitoringConfig monitoringConfig : update)
        {
            final AttributeDetails details = monitoringConfig.getAttributeDetails();
            final Set<AgentState> agentStates = agentIdListByAttribute.get(details);
            // TODO remove config if previously monitored
            agentStates.clear();
            agentStates.addAll(monitoringConfig.getAgentStates());
            for (AgentState agentState : monitoringConfig.getAgentStates())
            {
                if(agentState.isMonitored())
                {
                    if(details.isCompositeData())
                    {
                        agents.monitorNamedCompositeAttribute(agentState.getAgentId(), details.getDefaultLogicalName(),
                                toExactMatch(details.getObjectName()), toExactMatch(details.getAttributeName()),
                                details.getCompositeKey());
                    }
                    else
                    {
                        agents.monitorNamedAttribute(agentState.getAgentId(), details.getDefaultLogicalName(),
                                toExactMatch(details.getObjectName()), toExactMatch(details.getAttributeName()));
                    }
                }
            }
        }
    }

    private String toExactMatch(final String input)
    {
        return "^" + input + "$";
    }
}
