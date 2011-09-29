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

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.io.CodeBook;
import com.epickrram.monitaur.common.io.EncoderStream;
import com.epickrram.monitaur.common.io.PackerEncoderStream;
import com.epickrram.monitaur.common.logging.Logger;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

public final class MulticastPublisher implements Publisher
{
    private static final Logger LOGGER = Logger.getLogger(MulticastPublisher.class);
    private final CodeBook<String> codeBook;
    private final SocketAddress multicastAddress;
    private final MulticastSocket multicastSocket;

    public MulticastPublisher(final CodeBook<String> codeBook,
                              final InetAddress multicastAddress,
                              final int port)
    {
        this.codeBook = codeBook;
        this.multicastAddress = new InetSocketAddress(multicastAddress, port);
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