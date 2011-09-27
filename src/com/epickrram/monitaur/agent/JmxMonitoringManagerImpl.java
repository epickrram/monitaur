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

import com.epickrram.monitaur.common.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

public final class JmxMonitoringManagerImpl implements JmxMonitoringRequestListener
{
    private static Logger LOGGER = Logger.getLogger(JmxMonitoringManagerImpl.class);
    static final String OBJECT_NAME = "com.epickrram.monitaur:type=JmxMonitoringManagerImpl";

    private final JmxMonitoringRequestListener delegate;

    public JmxMonitoringManagerImpl(final JmxMonitoringRequestListener delegate)
    {
        this.delegate = delegate;
    }

    public void registerSelf()
    {
        try
        {
            final StandardMBean mBean = new StandardMBean(this, JmxMonitoringRequestListener.class, false);
            ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, new ObjectName(OBJECT_NAME));
        }
        catch (InstanceAlreadyExistsException e)
        {
            LOGGER.error("Failed to register JmxMonitoringManagerImpl", e);
        }
        catch (MBeanRegistrationException e)
        {
            LOGGER.error("Failed to register JmxMonitoringManagerImpl", e);
        }
        catch (NotCompliantMBeanException e)
        {
            LOGGER.error("Failed to register JmxMonitoringManagerImpl", e);
        }
        catch (MalformedObjectNameException e)
        {
            LOGGER.error("Failed to register JmxMonitoringManagerImpl", e);
        }
    }

    @Override
    public void monitorNamedAttribute(final String logicalName, final String objectNameRegex, final String attributeNameRegex)
    {
        delegate.monitorNamedAttribute(logicalName, objectNameRegex, attributeNameRegex);
    }

    @Override
    public void monitorNamedCompositeAttribute(final String logicalName, final String objectNameRegex,
                                               final String attributeNameRegex, final String compositeKey)
    {
        delegate.monitorNamedCompositeAttribute(logicalName, objectNameRegex, attributeNameRegex, compositeKey);
    }
}