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

import com.epickrram.freewheel.protocol.CodeBookImpl;
import com.epickrram.monitaur.agent.instrumentation.LatencyPublisher;
import com.epickrram.monitaur.agent.instrumentation.Transformer;
import com.epickrram.monitaur.common.Agents;
import com.epickrram.monitaur.common.AvailableAttributes;
import com.epickrram.monitaur.common.FreewheelMessagingHelperFactory;
import com.epickrram.monitaur.common.MessagingHelper;
import com.epickrram.monitaur.common.Server;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.jmx.AttributeDetails;

import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public final class MonitoringAgent 
{
    public static void premain(final String agentArgs, final Instrumentation instrumentation)
    {
        final CodeBookImpl codeBook = new CodeBookImpl();
//        instrumentation.addTransformer(new TransferrableFinder(codeBook));
        final ReentrantLock lock = new ReentrantLock();
//        instrumentation.addTransformer(createLatencyMonitorClassTransformer(lock));
        startJmxMonitoring(codeBook, lock);
    }

    private static Transformer createLatencyMonitorClassTransformer()
    {
        final String latencyPublisherClass = System.getProperty("com.epickrram.monitaur.latency.LatencyPublisherClass");
        if(latencyPublisherClass != null)
        {
            try
            {
                System.err.println("Creating LatencyPublisher of type: " + latencyPublisherClass);
                final LatencyPublisher latencyPublisher = (LatencyPublisher)
                        Class.forName(latencyPublisherClass).
                                newInstance();

                return new Transformer(latencyPublisher);
            }
            catch (InstantiationException e)
            {
                throw new IllegalStateException("Cannot instantiate LatencyPublisher", e);
            }
            catch (IllegalAccessException e)
            {
                throw new IllegalStateException("Cannot instantiate LatencyPublisher", e);
            }
            catch (ClassNotFoundException e)
            {
                throw new IllegalStateException("Cannot instantiate LatencyPublisher", e);
            }
        }
        return new Transformer(null);
    }

    static void startJmxMonitoring(final CodeBookImpl codeBook, final ReentrantLock lock)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // TODO this should be done by agent code
                    final CodeBookImpl.CodeBookRegistryImpl codeBookRegistry = new CodeBookImpl.CodeBookRegistryImpl(codeBook);
                    codeBookRegistry.registerTranslatable(MonitorData.class);
                    codeBookRegistry.registerTranslatable(AvailableAttributes.class);
                    codeBookRegistry.registerTranslatable(AttributeDetails.class);

                    final MessagingHelper messagingHelper =
                            new FreewheelMessagingHelperFactory(InetAddress.getByName("239.0.0.1"), 14001, codeBook).
                                            createMessagingHelper();


                    final Server server = messagingHelper.createPublisher(Server.class);

                    final JmxMonitoringAgent jmxMonitoringAgent = new JmxMonitoringAgent(server);

                    messagingHelper.registerSubscriber(Agents.class, jmxMonitoringAgent);
                    messagingHelper.start();

                    jmxMonitoringAgent.start(Executors.newSingleThreadScheduledExecutor());
                    

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}