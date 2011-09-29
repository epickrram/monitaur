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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MulticastPublisherIntegrationTest
{
    private List<MulticastReceiver> receiverList = new ArrayList<MulticastReceiver>();
    private ClassnameCodeBook codeBook = new ClassnameCodeBook();
    private ExecutorService executorService;
    private MonitorData data1;
    private MonitorData data2;
    private InetAddress inetAddress;

    @Test
    public void shouldPublishMessagesToMulticastAddress() throws Exception
    {
        final int port = 14000;
        final MulticastPublisher publisher = createPublisher(port);
        final int numberOfMessages = 2;
        final StubMonitorDataHandler handler = createReceiver(port, numberOfMessages);

        publisher.publish(data1);
        publisher.publish(data2);

        handler.getMessageReceivedLatch().await();
        assertThat(data1, equalTo(handler.getReceivedMessages().get(0)));
        assertThat(data2, equalTo(handler.getReceivedMessages().get(1)));
        assertThat(2, is(handler.getReceivedMessages().size()));
    }

    @Test
    public void shouldPublishMessagesToSpecifiedPortOnly() throws Exception
    {
        final int numberOfMessages = 1;
        final int port = 14000;
        final MulticastPublisher publisher = createPublisher(port);
        final StubMonitorDataHandler handler = createReceiver(port, numberOfMessages);
        final int port2 = 14010;
        final MulticastPublisher publisher2 = createPublisher(port2);
        final StubMonitorDataHandler handler2 = createReceiver(port2, numberOfMessages);

        publisher.publish(data1);
        publisher2.publish(data2);

        handler.getMessageReceivedLatch().await();
        handler2.getMessageReceivedLatch().await();

        assertThat(data1, equalTo(handler.getReceivedMessages().get(0)));
        assertThat(1, is(handler.getReceivedMessages().size()));
        assertThat(data2, equalTo(handler2.getReceivedMessages().get(0)));
        assertThat(1, is(handler2.getReceivedMessages().size()));
    }

    @Before
    public void before() throws Exception
    {
        executorService = Executors.newCachedThreadPool();
     
        final MonitorData.WireFormat wireFormat = new MonitorData.WireFormat();
        codeBook.registerHandlers(MonitorData.class.getName(), wireFormat, wireFormat);
        data1 = new MonitorData(MonitorType.SCALAR, "logicalName1", "hostname", Integer.MAX_VALUE, System.currentTimeMillis());
        data2 = new MonitorData(MonitorType.SCALAR, "logicalName2", "hostname", "foobar", System.currentTimeMillis());
        inetAddress = InetAddress.getByName("239.0.0.1");
    }

    @After
    public void after() throws Exception
    {
        for (MulticastReceiver multicastReceiver : receiverList)
        {
            multicastReceiver.stop();
        }
        executorService.shutdown();
    }

    private StubMonitorDataHandler createReceiver(final int port, final int expectedMessageCount) throws InterruptedException
    {
        final StubMonitorDataHandler dataHandler = new StubMonitorDataHandler(expectedMessageCount);
        final MulticastReceiver multicastReceiver = new MulticastReceiver(codeBook, inetAddress, port, executorService, dataHandler);
        multicastReceiver.start();
        receiverList.add(multicastReceiver);
        return dataHandler;
    }

    private MulticastPublisher createPublisher(final int port)
    {
        return new MulticastPublisher(codeBook, inetAddress, port);
    }

    private static final class StubMonitorDataHandler implements MonitorDataHandler
    {
        private final CountDownLatch messageReceivedLatch;
        private final List<MonitorData> receivedMessages = new CopyOnWriteArrayList<MonitorData>();

        public StubMonitorDataHandler(final int expectedMessageCount)
        {
            this.messageReceivedLatch = new CountDownLatch(expectedMessageCount);
        }

        @Override
        public void onMonitorData(final MonitorData data)
        {
            receivedMessages.add(data);
            this.messageReceivedLatch.countDown();
        }

        public List<MonitorData> getReceivedMessages()
        {
            return receivedMessages;
        }

        public CountDownLatch getMessageReceivedLatch()
        {
            return messageReceivedLatch;
        }
    }
}