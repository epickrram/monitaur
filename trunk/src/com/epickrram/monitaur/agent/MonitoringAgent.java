package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.collector.JmxCollector;
import com.epickrram.monitaur.agent.config.ConfigParser;
import com.epickrram.monitaur.agent.instrumentation.Transformer;
import com.epickrram.monitaur.common.logging.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
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
                    LOGGER.info("Waiting 30 seconds for MBeans to register");
                    Thread.sleep(30000L);
                    try
                    {
                        final ConfigParser configParser = new ConfigParser();
                        final File file = new File(System.getProperty("monitaur.agent.config"));
                        final InputStream resourceAsStream = new FileInputStream(file);

                        configParser.parse(resourceAsStream);
                        final Collection<JmxCollector> jmxCollectors = configParser.getJmxCollectors();
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
                            publisher = new StdOutPublisher();
                        }

                        final JmxMonitoringAgent jmxMonitoringAgent = new JmxMonitoringAgent(publisher);
                        jmxMonitoringAgent.addCollectors(jmxCollectors);
                        jmxMonitoringAgent.start(Executors.newSingleThreadScheduledExecutor());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}