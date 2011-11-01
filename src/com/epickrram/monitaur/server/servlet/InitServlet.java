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
package com.epickrram.monitaur.server.servlet;

import com.epickrram.freewheel.protocol.CodeBookImpl;
import com.epickrram.monitaur.common.Agents;
import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.FreewheelMessagingHelperFactory;
import com.epickrram.monitaur.common.MessagingHelper;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.AttributeDetails;
import com.epickrram.monitaur.server.Context;
import com.epickrram.monitaur.server.MonitorDataStore;
import com.epickrram.monitaur.server.ServerConfig;
import com.epickrram.monitaur.server.ServerImpl;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class InitServlet extends GenericServlet
{
    private static Context context;

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            // TODO Freewheel should provide a CodeBook context to return a CodeBook and a CodeBookRegistry

            final CodeBookImpl codeBook = new CodeBookImpl();
            final CodeBookImpl.CodeBookRegistryImpl codeBookRegistry = new CodeBookImpl.CodeBookRegistryImpl(codeBook);
            codeBookRegistry.registerTranslatable(MonitorData.class);
            codeBookRegistry.registerTranslatable(AvailableAttributes.class);
            codeBookRegistry.registerTranslatable(AttributeDetails.class);

            final MessagingHelper messagingHelper = new FreewheelMessagingHelperFactory(InetAddress.getByName("239.0.0.1"),
                    14001, codeBook).createMessagingHelper();

            final MonitorDataStore monitorDataStore = new MonitorDataStore(1000);

            final Agents agents = messagingHelper.createPublisher(Agents.class);

            final ServerConfig serverConfig = new ServerConfig(agents);
            final ServerImpl server = new ServerImpl(monitorDataStore, serverConfig);

            messagingHelper.registerSubscriber(Server.class, server);

            messagingHelper.start();

            context = new Context(monitorDataStore, messagingHelper, agents, serverConfig);

            startAgentAttributePollTask(context);

            ContextFilter.setContext(context);
        }
        catch (UnknownHostException e)
        {
            throw new ServletException("Unable to initialise data receiver", e);
        }
    }

    private void startAgentAttributePollTask(final Context context)
    {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                context.getAgents().publishAvailableAttributes();
            }
        }, 5L, 10L, TimeUnit.SECONDS);
    }

    @Override
    public void destroy()
    {
        if(context != null)
        {
            context.getMessagingHelper().stop();
        }
    }

    @Override
    public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException
    {
    }
}
