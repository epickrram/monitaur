package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.GaugeData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.management.MBeanServerConnection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class GaugeCollectorTest
{
    private static final Number MINIMUM_VALUE = 56L;
    private static final Number MAXIMUM_VALUE = 127L;
    private static final Number CURRENT_VALUE = 100L;

    @Mock
    private JmxCollector minimum;
    @Mock
    private JmxCollector maximum;
    @Mock
    private JmxCollector current;
    private GaugeCollector gaugeCollector;

    @Test
    public void shouldDetermineGaugeValueFromDelegates() throws Exception
    {
        when(minimum.getType()).thenReturn(DataType.LONG);
        when(maximum.getType()).thenReturn(DataType.LONG);
        when(current.getType()).thenReturn(DataType.LONG);

        when(minimum.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(MINIMUM_VALUE);
        when(maximum.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(MAXIMUM_VALUE);
        when(current.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(CURRENT_VALUE);

        gaugeCollector = new GaugeCollector("foo", minimum, current, maximum);

        final GaugeData gaugeData = gaugeCollector.getValue(null);

        assertThat(gaugeData.getMinimum(), is(MINIMUM_VALUE));
        assertThat(gaugeData.getCurrentValue(), is(CURRENT_VALUE));
        assertThat(gaugeData.getMaximum(), is(MAXIMUM_VALUE));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateThatAllTypesAreTheSame() throws Exception
    {
        when(minimum.getType()).thenReturn(DataType.LONG);
        when(maximum.getType()).thenReturn(DataType.LONG);
        when(current.getType()).thenReturn(DataType.STRING);

        new GaugeCollector("foo", minimum, current, maximum);
    }
}