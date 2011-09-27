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
