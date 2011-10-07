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
package com.epickrram.monitaur.agent;

import com.epickrram.freewheel.io.ClassnameCodeBook;
import com.epickrram.freewheel.messaging.MessagingService;
import com.epickrram.freewheel.messaging.MessagingServiceImpl;
import com.epickrram.freewheel.messaging.Receiver;
import com.epickrram.freewheel.remoting.ClassNameTopicIdGenerator;
import com.epickrram.freewheel.remoting.PublisherFactory;
import com.epickrram.freewheel.remoting.SubscriberFactory;
import com.epickrram.monitaur.agent.instrumentation.Transformer;
import com.epickrram.monitaur.common.Agents;
import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.instrumentation.TransferrableFinder;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.util.concurrent.Executors;

public final class MonitoringAgent 
{
    public static void premain(final String agentArgs, final Instrumentation instrumentation)
    {
        final ClassnameCodeBook classnameCodeBook = new ClassnameCodeBook();
        instrumentation.addTransformer(new TransferrableFinder(classnameCodeBook));
        instrumentation.addTransformer(new Transformer());
        startJmxMonitoring(classnameCodeBook);
    }

    static void startJmxMonitoring(final ClassnameCodeBook codeBook)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // TODO this should be done by agent code
                    codeBook.registerTranscoder(MonitorData.class.getName(), new MonitorData.Transcoder());
                    codeBook.registerTranscoder(AvailableAttributes.class.getName(), new AvailableAttributes.Transcoder());
                    codeBook.registerTranscoder(AttributeDetails.class.getName(), new AttributeDetails.Transcoder());

                    final MessagingService messagingService = new MessagingServiceImpl(InetAddress.getByName("239.0.0.1").getHostAddress(), 14001, codeBook);
                    final ClassNameTopicIdGenerator topicIdGenerator = new ClassNameTopicIdGenerator();
                    final Server server = new PublisherFactory(messagingService, topicIdGenerator, codeBook).createPublisher(Server.class);

                    final JmxMonitoringAgent jmxMonitoringAgent = new JmxMonitoringAgent(server);

                    final Receiver receiver = new SubscriberFactory().createReceiver(Agents.class, jmxMonitoringAgent);
                    messagingService.registerReceiver(topicIdGenerator.getTopicId(Agents.class), receiver);

                    messagingService.start();

                    jmxMonitoringAgent.start(Executors.newSingleThreadScheduledExecutor());
//                    jmxMonitoringAgent.monitorNamedCompositeAttribute("HeapUsedBytes", ".*Memory$", "^HeapMemoryUsage$", "used");
//                    jmxMonitoringAgent.monitorNamedCompositeAttribute("EdenUsedBytes", ".*PS Eden Space", "^Usage$", "used");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}