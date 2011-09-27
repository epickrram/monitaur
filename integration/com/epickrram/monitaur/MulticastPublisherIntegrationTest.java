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
package com.epickrram.monitaur;

import com.epickrram.monitaur.agent.MulticastPublisher;
import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.domain.MonitorType;
import com.epickrram.monitaur.common.io.ClassnameCodeBook;
import com.epickrram.monitaur.server.MonitorDataHandler;
import com.epickrram.monitaur.server.MulticastReceiver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MulticastPublisherIntegrationTest
{
    private MulticastPublisher multicastPublisher;
    private InetSocketAddress socketAddress;
    private ClassnameCodeBook codeBook = new ClassnameCodeBook();
    private ExecutorService executorService;
    private MulticastReceiver multicastReceiver;
    private CountDownLatch messageReceivedLatch = new CountDownLatch(2);

    @Test
    public void shouldPublishMessagesToMulticastAddress() throws Exception
    {
        multicastReceiver.start();

        multicastPublisher.publish(new MonitorData(MonitorType.SCALAR, "logicalName1", "hostname", Integer.MAX_VALUE, System.currentTimeMillis()));
        multicastPublisher.publish(new MonitorData(MonitorType.SCALAR, "logicalName2", "hostname", "foobar", System.currentTimeMillis()));

        messageReceivedLatch.await();
    }

    @Before
    public void before() throws Exception
    {
        socketAddress = new InetSocketAddress(InetAddress.getByName("239.0.0.1"), 14000);
        multicastPublisher = new MulticastPublisher(codeBook, socketAddress, 14000, "239.0.0.1");
        executorService = Executors.newSingleThreadExecutor();
        multicastReceiver = new MulticastReceiver(codeBook, socketAddress, executorService, new StubMonitorDataHandler());

        final MonitorData.WireFormat wireFormat = new MonitorData.WireFormat();
        codeBook.registerHandlers(MonitorData.class.getName(), wireFormat, wireFormat);
    }

    @After
    public void after() throws Exception
    {
        multicastReceiver.stop();
        executorService.shutdown();
    }

    private class StubMonitorDataHandler implements MonitorDataHandler
    {
        @Override
        public void onMonitorData(final MonitorData data)
        {
            System.out.println("Received monitorData: " + data);
            messageReceivedLatch.countDown();
        }
    }
}