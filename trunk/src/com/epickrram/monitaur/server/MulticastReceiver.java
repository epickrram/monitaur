package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.io.CodeBook;
import com.epickrram.monitaur.common.io.DecoderStream;
import com.epickrram.monitaur.common.io.UnpackerDecoderStream;
import com.epickrram.monitaur.common.logging.Logger;
import org.msgpack.unpacker.MessagePackUnpacker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.epickrram.monitaur.common.io.NetUtil.getMulticastCapableAddress;

public final class MulticastReceiver
{
    private static final Logger LOGGER = Logger.getLogger(MulticastReceiver.class);
    private final CodeBook<String> codeBook;
    private final MulticastSocket multicastSocket;
    private final MonitorDataHandler handler;
    private final ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public MulticastReceiver(final CodeBook<String> codeBook,
                             final SocketAddress multicastAddress,
                             final ExecutorService executorService,
                             final MonitorDataHandler handler)
    {
        this.codeBook = codeBook;
        this.executorService = executorService;
        this.handler = handler;
        try
        {
            multicastSocket = new MulticastSocket(multicastAddress);
            multicastSocket.joinGroup(multicastAddress, getMulticastCapableAddress());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to create MulticastSocket", e);
        }
    }

    public void start() throws InterruptedException
    {
        LOGGER.info("Starting receiver");
        final CountDownLatch latch = new CountDownLatch(1);
        executorService.submit(new ReceiverJob(latch));
        latch.await();
    }

    public void stop()
    {
        running.set(false);
    }

    private final class ReceiverJob implements Runnable
    {
        private static final int BUFFER_SIZE = 4096;
        private final byte[] buffer = new byte[BUFFER_SIZE];
        private final CountDownLatch latch;

        public ReceiverJob(final CountDownLatch latch)
        {
            this.latch = latch;
        }

        public void run()
        {
            latch.countDown();
            while(running.get() && !Thread.currentThread().isInterrupted())
            {
                try
                {
                    final DatagramPacket packet = new DatagramPacket(buffer, 0, BUFFER_SIZE);
                    LOGGER.info("Receiving packet");
                    multicastSocket.receive(packet);
                    LOGGER.info("Received packet");

                    final MessagePackUnpacker unpacker = new MessagePackUnpacker(new ByteArrayInputStream(buffer, packet.getOffset(), packet.getLength()));
                    final DecoderStream decoderStream = new UnpackerDecoderStream(codeBook, unpacker);
                    final MonitorData monitorData = decoderStream.readObject();
                    handler.onMonitorData(monitorData);
                }
                catch(IOException e)
                {
                    LOGGER.error("Failed to process incoming data", e);
                }
            }
            LOGGER.info("Receiver stopping, Thread interrupted: " + Thread.currentThread().isInterrupted());
        }
    }
}
