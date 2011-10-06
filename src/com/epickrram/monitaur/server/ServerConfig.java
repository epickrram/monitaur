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

    private final ConcurrentMap<AttributeDetails, Set<AgentState>> agentIdListByAttibute =
            new ConcurrentHashMap<AttributeDetails, Set<AgentState>>();

    private volatile Agents agents;

    public ServerConfig()
    {
    }

    public void setAgents(final Agents agents)
    {
        this.agents = agents;
    }

    public void addAvailableAgentAttribute(final AttributeDetails attributeDetails, final String agentId)
    {
        Set<AgentState> agentIdList = agentIdListByAttibute.get(attributeDetails);
        if(agentIdList == null)
        {
            agentIdList = new CopyOnWriteArraySet<AgentState>();
            final Set<AgentState> existing = agentIdListByAttibute.putIfAbsent(attributeDetails, agentIdList);
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
        return agentIdListByAttibute;
    }

    public void updateAgentIdListByAttribute(final MonitoringConfig[] update)
    {
        for (MonitoringConfig monitoringConfig : update)
        {
            final AttributeDetails details = new AttributeDetails(monitoringConfig.getObjectName(), monitoringConfig.getAttributeName());
            agentIdListByAttibute.get(details).addAll(monitoringConfig.getAgentStates());
            for (AgentState agentState : monitoringConfig.getAgentStates())
            {
                if(agentState.isMonitored())
                {
                    agents.monitorNamedAttribute(agentState.getAgentId(), monitoringConfig.getAttributeName(),
                            monitoringConfig.getObjectName(), monitoringConfig.getAttributeName());
                }
            }
        }
    }
}
