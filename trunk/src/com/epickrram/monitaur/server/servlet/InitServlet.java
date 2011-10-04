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

import com.epickrram.freewheel.io.ClassnameCodeBook;
import com.epickrram.freewheel.messaging.MessagingServiceImpl;
import com.epickrram.freewheel.messaging.Receiver;
import com.epickrram.freewheel.remoting.ClassNameTopicIdGenerator;
import com.epickrram.freewheel.remoting.SubscriberFactory;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.server.MonitorDataStore;
import com.epickrram.monitaur.server.ServerImpl;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class InitServlet extends GenericServlet
{
    private static Context context;

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            
            final ClassnameCodeBook codeBook = new ClassnameCodeBook();
            final MonitorData.Transcoder transcoder = new MonitorData.Transcoder();
            codeBook.registerTranscoder(MonitorData.class.getName(), transcoder);

            final MonitorDataStore monitorDataStore = new MonitorDataStore(1000);
            final ServerImpl server = new ServerImpl(monitorDataStore);
            final MessagingServiceImpl messagingService =
                    new MessagingServiceImpl(InetAddress.getByName("239.0.0.1").getHostAddress(), 14001, codeBook);
            final Receiver receiver = new SubscriberFactory().createReceiver(Server.class, server);
            // TODO should be property of Receiver
            final int topicId = new ClassNameTopicIdGenerator().getTopicId(Server.class);
            messagingService.registerReceiver(topicId, receiver);

            messagingService.start();


            context = new Context(monitorDataStore, messagingService);
            ContextFilter.setContext(context);
        }
        catch (UnknownHostException e)
        {
            throw new ServletException("Unable to initialise data receiver", e);
        }
    }

    @Override
    public void destroy()
    {
        if(context != null)
        {
            context.getMessageService().shutdown();
        }
    }

    @Override
    public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException
    {
    }
}
