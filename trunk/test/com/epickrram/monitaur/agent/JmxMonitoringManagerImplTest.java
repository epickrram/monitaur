package com.epickrram.monitaur.agent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.management.ObjectName;

import java.lang.management.ManagementFactory;

import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public final class JmxMonitoringManagerImplTest
{
    private static final String JAVA_LANG_STRING = "java.lang.String";
    private static final String OBJECT_NAME_REGEX = "objectNameRegex";
    private static final String ATTRIBUTE_NAME_REGEX = "attributeNameRegex";
    private static final String LOGICAL_NAME = "logicalName";
    private static final String COMPOSITE_KEY = "key";

    @Mock
    private JmxMonitoringRequestListener jmxMonitoringRequestListener;
    private JmxMonitoringManagerImpl jmxMonitoringManager;

    @Test
    public void shouldCallDelegateToMonitorNamedAttribute() throws Exception
    {
        getPlatformMBeanServer().invoke(new ObjectName(JmxMonitoringManagerImpl.OBJECT_NAME),
                "monitorNamedAttribute", new Object[] {LOGICAL_NAME, OBJECT_NAME_REGEX, ATTRIBUTE_NAME_REGEX},
                new String[] {JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING});

        verify(jmxMonitoringRequestListener).monitorNamedAttribute(LOGICAL_NAME, OBJECT_NAME_REGEX, ATTRIBUTE_NAME_REGEX);
    }

    @Test
    public void shouldCallDelegateToMonitorNamedCompositeAttribute() throws Exception
    {
        getPlatformMBeanServer().invoke(new ObjectName(JmxMonitoringManagerImpl.OBJECT_NAME),
                "monitorNamedCompositeAttribute", new Object[] {LOGICAL_NAME, OBJECT_NAME_REGEX, ATTRIBUTE_NAME_REGEX, COMPOSITE_KEY},
                new String[] {JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING, JAVA_LANG_STRING});

        verify(jmxMonitoringRequestListener).monitorNamedCompositeAttribute(LOGICAL_NAME, OBJECT_NAME_REGEX,
                ATTRIBUTE_NAME_REGEX, COMPOSITE_KEY);
    }

    @Before
    public void setUp() throws Exception
    {
        jmxMonitoringManager = new JmxMonitoringManagerImpl(jmxMonitoringRequestListener);
        jmxMonitoringManager.registerSelf();
    }

    @After
    public void tearDown() throws Exception
    {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(JmxMonitoringManagerImpl.OBJECT_NAME));
    }
}