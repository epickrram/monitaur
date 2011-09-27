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
package com.epickrram.monitaur.agent.collector;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public final class ConstantNumberCollectorTest
{

    private static final String LOGICAL_NAME = "logicalName";

    @Test
    public void shouldReturnInt() throws Exception
    {
        assertCollectorReturnsValue(5);
    }

    @SuppressWarnings({"UnnecessaryBoxing"})
    @Test
    public void shouldReturnInteger() throws Exception
    {
        assertCollectorReturnsValue(Integer.valueOf(5));
    }

    @Test
    public void shouldReturnPrimitiveLong() throws Exception
    {
        assertCollectorReturnsValue(5L);
    }

    @SuppressWarnings({"UnnecessaryBoxing"})
    @Test
    public void shouldReturnLong() throws Exception
    {
        assertCollectorReturnsValue(Long.valueOf(5L));
    }

    @Test
    public void shouldReturnPrimitiveFloat() throws Exception
    {
        assertCollectorReturnsValue(5.345f);
    }

    @SuppressWarnings({"UnnecessaryBoxing"})
    @Test
    public void shouldReturnFloat() throws Exception
    {
        assertCollectorReturnsValue(Float.valueOf(5.345f));
    }

    @Test
    public void shouldReturnPrimitiveDouble() throws Exception
    {
        assertCollectorReturnsValue(5.345d);
    }

    @SuppressWarnings({"UnnecessaryBoxing"})
    @Test
    public void shouldReturnDouble() throws Exception
    {
        assertCollectorReturnsValue(Double.valueOf(5.345d));
    }

    @SuppressWarnings({"unchecked"})
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTypeCannotBeDetermined() throws Exception
    {
        new ConstantNumberCollector(LOGICAL_NAME, new Byte((byte) 0xff));
    }

    private <T extends Number> void assertCollectorReturnsValue(final T value)
    {
        final ConstantNumberCollector<?> collector = createCollector(value);

        assertThat(value, is(collector.getValue(null)));
    }

    private <T extends Number> ConstantNumberCollector<T> createCollector(final T value)
    {
        return new ConstantNumberCollector<T>(LOGICAL_NAME, value);
    }
}