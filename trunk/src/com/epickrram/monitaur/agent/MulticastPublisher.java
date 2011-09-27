package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.io.CodeBook;
import com.epickrram.monitaur.common.io.EncoderStream;
import com.epickrram.monitaur.common.io.NetUtil;
import com.epickrram.monitaur.common.io.PackerEncoderStream;
import com.epickrram.monitaur.common.logging.Logger;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

import static com.epickrram.monitaur.common.io.NetUtil.getMulticastCapableAddress;

public final class MulticastPublisher implements Publisher
{
    private static final Logger LOGGER = Logger.getLogger(MulticastPublisher.class);
    private final CodeBook<String> codeBook;
    private final SocketAddress multicastAddress;
    private final int port;
    private final String ipAddress;
    private final MulticastSocket multicastSocket;

    public MulticastPublisher(final CodeBook<String> codeBook,
                              final SocketAddress multicastAddress,
                              final int port,
                              final String ipAddress)
    {
        this.codeBook = codeBook;
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.ipAddress = ipAddress;
        try
        {
            multicastSocket = new MulticastSocket(port);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to create MulticastSocket", e);
        }
    }

    public void publish(final MonitorData data)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Packer packer = new MessagePackPacker(outputStream);
        final EncoderStream encoderStream = new PackerEncoderStream(codeBook, packer);
        try
        {
            encoderStream.writeObject(data);
            LOGGER.info("Created a message of " + outputStream.size() + " bytes");
            final DatagramPacket packet = new DatagramPacket(outputStream.toByteArray(), 0, outputStream.size());
            packet.setSocketAddress(multicastAddress);
            LOGGER.info("Sending packet");
            multicastSocket.send(packet);
            LOGGER.info("Sent packet");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to publish message", e);
        }
    }
}