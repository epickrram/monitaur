package com.epickrram.monitaur.common.io;

import com.epickrram.monitaur.common.logging.Logger;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;

public final class NetUtil
{
    private static final Logger LOGGER = Logger.getLogger(NetUtil.class);

    private NetUtil() {}

    public static NetworkInterface getMulticastCapableAddress() throws IOException
    {
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while(networkInterfaces.hasMoreElements())
        {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            if(networkInterface.supportsMulticast())
            {
                LOGGER.info("Using multicast interface: " + networkInterface.getDisplayName());
                return networkInterface;
            }
        }
        throw new IOException("Could not find multicast-capable interface");
    }
}
