/*
Copyright 2011 Mark Price

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.epickrram.monitaur.server;

import com.epickrram.freewheel.messaging.MessagingService;
import com.epickrram.monitaur.common.Agents;

public final class Context
{
    public static final String REQUEST_ATTRIBUTE_KEY = Context.class.getName();

    private final MonitorDataStore monitorDataStore;
    private final MessagingService messageService;
    private final Agents agents;
    private final ServerConfig serverConfig;

    public Context(final MonitorDataStore monitorDataStore,
                   final MessagingService messageService,
                   final Agents agents,
                   final ServerConfig serverConfig)
    {
        this.monitorDataStore = monitorDataStore;
        this.messageService = messageService;
        this.agents = agents;
        this.serverConfig = serverConfig;
    }

    public MonitorDataStore getMonitorDataStore()
    {
        return monitorDataStore;
    }

    public MessagingService getMessageService()
    {
        return messageService;
    }

    public Agents getAgents()
    {
        return agents;
    }

    public ServerConfig getServerConfig()
    {
        return serverConfig;
    }
}
