package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.GaugeData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.management.MBeanServerConnection;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class GaugeCollectorTest
{
    private static final Number MINIMUM_VALUE = 56;
    private static final Number MAXIMUM_VALUE = 127;
    private static final Number CURRENT_VALUE = 100;

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
        when(minimum.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(MINIMUM_VALUE);
        when(maximum.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(MAXIMUM_VALUE);
        when(current.getValue(Matchers.<MBeanServerConnection>any())).thenReturn(CURRENT_VALUE);
        gaugeCollector = new GaugeCollector("foo", minimum, current, maximum);

        final GaugeData gaugeData = gaugeCollector.getValue(null);

        Assert.assertThat(gaugeData.getMinimum(), is(MINIMUM_VALUE));
        Assert.assertThat(gaugeData.getCurrentValue(), is(CURRENT_VALUE));
        Assert.assertThat(gaugeData.getMaximum(), is(MAXIMUM_VALUE));
    }
}