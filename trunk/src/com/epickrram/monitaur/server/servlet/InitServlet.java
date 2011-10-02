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
package com.epickrram.monitaur.server.servlet;

import com.epickrram.monitaur.common.domain.MonitorData;
import com.epickrram.monitaur.common.io.ClassnameCodeBook;
import com.epickrram.monitaur.server.MonitorDataStore;
import com.epickrram.monitaur.server.MulticastReceiver;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public final class InitServlet extends GenericServlet
{
    private static Context context;

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            
            final ClassnameCodeBook codeBook = new ClassnameCodeBook();
            final MonitorData.Translator translator = new MonitorData.Translator();
            codeBook.registerHandlers(MonitorData.class.getName(), translator, translator);

            final MonitorDataStore monitorDataStore = new MonitorDataStore(1000);
            final MulticastReceiver multicastReceiver = new MulticastReceiver(codeBook,
                    InetAddress.getByName("239.0.0.1"), 14001,
                    Executors.newCachedThreadPool(), monitorDataStore);
            multicastReceiver.start();
            context = new Context(monitorDataStore, multicastReceiver);
            ContextFilter.setContext(context);
        }
        catch (UnknownHostException e)
        {
            throw new ServletException("Unable to initialise data receiver", e);
        }
        catch (InterruptedException e)
        {
            throw new ServletException("Unable to initialise data receiver", e);
        }
    }

    @Override
    public void destroy()
    {
        if(context != null)
        {
            context.getMulticastReceiver().stop();
        }
    }

    @Override
    public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException
    {
    }
}
