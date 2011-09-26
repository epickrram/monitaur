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