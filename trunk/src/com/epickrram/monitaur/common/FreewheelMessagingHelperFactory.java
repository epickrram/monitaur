package com.epickrram.monitaur.common;

import com.epickrram.freewheel.messaging.MessagingServiceImpl;
import com.epickrram.freewheel.protocol.CodeBook;

import java.net.InetAddress;

public final class FreewheelMessagingHelperFactory implements MessagingHelperFactory
{
    private final InetAddress multicastAddress;
    private final int port;
    private final CodeBook<String> codeBook;

    public FreewheelMessagingHelperFactory(final InetAddress multicastAddress, final int port, final CodeBook<String> codeBook)
    {
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.codeBook = codeBook;
    }

    @Override
    public MessagingHelper createMessagingHelper()
    {
        return new FreewheelMessagingHelper(
                new MessagingServiceImpl(multicastAddress.getHostAddress(), port, codeBook), codeBook);
    }
}
