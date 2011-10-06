package com.epickrram.monitaur.server;

public final class AgentState
{
    private final String agentId;
    private final boolean monitored;

    public AgentState(final String agentId, final boolean monitored)
    {
        this.agentId = agentId;
        this.monitored = monitored;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public boolean isMonitored()
    {
        return monitored;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AgentState that = (AgentState) o;

        if (agentId != null ? !agentId.equals(that.agentId) : that.agentId != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return agentId != null ? agentId.hashCode() : 0;
    }
}
