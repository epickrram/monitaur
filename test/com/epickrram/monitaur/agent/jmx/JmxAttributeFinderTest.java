package com.epickrram.monitaur.agent.jmx;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public final class JmxAttributeFinderTest
{
    @Test
    public void shouldFindAttribute() throws Exception
    {
        assertNotNull(new JmxAttributeFinder("java\\.lang:type=Runtime", "^StartTime$").findAttribute());
    }

    @Test
    public void shouldReturnNullIfNoAttributeCanBeFound() throws Exception
    {
        assertNull(new JmxAttributeFinder("unknownMBean", "unknownAttribute").findAttribute());
    }
}
