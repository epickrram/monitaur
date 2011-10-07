package com.epickrram.monitaur.server.servlet;

import com.epickrram.freewheel.util.Logger;
import com.epickrram.monitaur.common.jmx.AttributeDetails;
import com.epickrram.monitaur.server.AgentState;
import com.epickrram.monitaur.server.Context;
import com.epickrram.monitaur.server.MonitoringConfig;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public final class ConfigServlet extends MonitaurServlet
{
    private static final Logger LOGGER = Logger.getLogger(ConfigServlet.class);

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        final Context context = getContext(request);
        final Gson gson = new Gson();

        if(request.getParameter("update") != null)
        {
            final MonitoringConfig[] update = gson.fromJson(request.getParameter("postData"), MonitoringConfig[].class);

            for (MonitoringConfig monitoringConfig : update)
            {
                for (AgentState agentState : monitoringConfig.getAgentStates())
                {
                    if(agentState.isMonitored())
                    {
                        LOGGER.info("Agent [" + agentState.getAgentId() +
                                "] is monitoring: " + monitoringConfig.getAttributeDetails());
                    }
                }
            }
            context.getServerConfig().updateAgentIdListByAttribute(update);
        }
        else
        {
            response.setContentType("text/json");
            final String json = gson.toJson(getMonitoringConfigList(context));
            response.setContentLength(json.length());
            response.getWriter().append(json);
            response.flushBuffer();
        }
    }

    private MonitoringConfig[] getMonitoringConfigList(final Context context)
    {
        final ConcurrentMap<AttributeDetails, Set<AgentState>> agentIdListByAttibute = context.getServerConfig().getAgentIdListByAttribute();
        final List<MonitoringConfig> monitoringConfigList = new ArrayList<MonitoringConfig>(agentIdListByAttibute.size());

        for (Map.Entry<AttributeDetails, Set<AgentState>> entry : agentIdListByAttibute.entrySet())
        {
            final AttributeDetails attributeDetails = entry.getKey();
            final MonitoringConfig monitoringConfig =
                    new MonitoringConfig(
                            attributeDetails, entry.getValue(), false);
            monitoringConfigList.add(monitoringConfig);
        }

        Collections.sort(monitoringConfigList);
        return monitoringConfigList.toArray(new MonitoringConfig[monitoringConfigList.size()]);
    }
}