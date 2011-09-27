package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.instrumentation.Transformer;
import com.epickrram.monitaur.common.logging.Logger;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.lang.instrument.Instrumentation;
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