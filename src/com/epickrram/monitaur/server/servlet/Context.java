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

import com.epickrram.freewheel.messaging.MessagingService;
import com.epickrram.freewheel.messaging.MessagingServiceImpl;
import com.epickrram.monitaur.server.MonitorDataStore;
import com.epickrram.monitaur.server.MulticastReceiver;

public final class Context
{
    public static final String REQUEST_ATTRIBUTE_KEY = Context.class.getName();

    private final MonitorDataStore monitorDataStore;
    private final MessagingService messageService;

    public Context(final MonitorDataStore monitorDataStore, final MessagingService messageService)
    {
        this.monitorDataStore = monitorDataStore;
        this.messageService = messageService;
    }

    public MonitorDataStore getMonitorDataStore()
    {
        return monitorDataStore;
    }

    public MessagingService getMessageService()
    {
        return messageService;
    }
}
