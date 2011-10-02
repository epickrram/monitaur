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

import com.epickrram.monitaur.agent.instrumentation.Transformer;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.io.ClassnameCodeBook;
import com.epickrram.monitaur.common.logging.Logger;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.util.concurrent.Executors;

public final class MonitoringAgent
{
    private static final Logger LOGGER = Logger.getLogger(MonitoringAgent.class);

    public static void premain(final String agentArgs, final Instrumentation instrumentation)
    {
        instrumentation.addTransformer(new Transformer());
        startJmxMonitoring();
    }

    static void startJmxMonitoring()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final Publisher publisher;
                    if(Boolean.valueOf(System.getProperty("monitaur.ui")))
                    {
                        final UiPublisher uiPublisher = new UiPublisher();
                        publisher = new CompositePublisher(new StdOutPublisher(), uiPublisher);
                        final JFrame frame = new JFrame();
                        final Container contentPane = frame.getContentPane();
                        contentPane.setLayout(new BorderLayout());
                        contentPane.add("Center", uiPublisher);
                        frame.setSize(400, 600);
                        frame.setVisible(true);
                    }
                    else
                    {
                        final ClassnameCodeBook codeBook = new ClassnameCodeBook();
                        final MonitorData.Translator translator = new MonitorData.Translator();
                        codeBook.registerHandlers(MonitorData.class.getName(), translator, translator);
                        publisher = new CompositePublisher(
                                new MulticastPublisher(codeBook, InetAddress.getByName("239.0.0.1"), 14001),
                                new StdOutPublisher());
                    }

                    final JmxMonitoringAgent jmxMonitoringAgent = new JmxMonitoringAgent(publisher);
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