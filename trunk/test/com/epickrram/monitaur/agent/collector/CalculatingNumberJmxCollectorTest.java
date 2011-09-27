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

import com.epickrram.monitaur.common.domain.DataType;
import com.epickrram.monitaur.common.domain.MonitorType;
import org.junit.Test;

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.CalculatingNumberJmxCollector.Operator.ADD;
import static com.epickrram.monitaur.agent.collector.CalculatingNumberJmxCollector.Operator.SUBTRACT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public final class CalculatingNumberJmxCollectorTest
{
    private static final String LOGICAL_NAME = "logicalName";

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateOperandTypes() throws Exception
    {
        new CalculatingNumberJmxCollector(LOGICAL_NAME,
                collectorOfType(DataType.STRING),
                collectorOfType(DataType.DOUBLE), ADD);
    }

    @Test
    public void shouldAddIntegerInteger() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11;

        final Number value = calculateValue(operandOne, operandTwo, ADD);

        assertThat(value.intValue(), equalTo(operandOne.intValue() + operandTwo.intValue()));
    }

    @Test
    public void shouldAddIntegerLong() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = Long.MAX_VALUE - 5000;

        final Number value = calculateValue(operandOne, operandTwo, ADD);

        assertThat(value.longValue(), equalTo(operandOne.intValue() + operandTwo.longValue()));
    }

    @Test
    public void shouldSubtractIntegerInteger() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11;

        final Number value = calculateValue(operandOne, operandTwo, SUBTRACT);

        assertThat(value.intValue(), equalTo(operandOne.intValue() - operandTwo.intValue()));
    }

    @Test
    public void shouldSubtractIntegerLong() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = Long.MAX_VALUE - 5000;

        final Number value = calculateValue(operandOne, operandTwo, SUBTRACT);

        assertThat(value.longValue(), equalTo(operandOne.intValue() - operandTwo.longValue()));
    }

    @Test
    public void shouldAddIntegerFloat() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11.34875f;

        final Number value = calculateValue(operandOne, operandTwo, ADD);

        assertThat(value.floatValue(), equalTo(operandOne.floatValue() + operandTwo.floatValue()));
    }

    @Test
    public void shouldAddLongDouble() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11.34875d;

        final Number value = calculateValue(operandOne, operandTwo, ADD);

        assertThat(value.floatValue(), equalTo(operandOne.floatValue() + operandTwo.floatValue()));
    }

    @Test
    public void shouldSubtractIntegerFloat() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11.34875f;

        final Number value = calculateValue(operandOne, operandTwo, SUBTRACT);

        assertThat(value.floatValue(), equalTo(operandOne.floatValue() - operandTwo.floatValue()));
    }

    @Test
    public void shouldSubtractLongDouble() throws Exception
    {
        final Number operandOne = 17;
        final Number operandTwo = 11.34875d;

        final Number value = calculateValue(operandOne, operandTwo, SUBTRACT);

        assertThat(value.floatValue(), equalTo(operandOne.floatValue() - operandTwo.floatValue()));
    }

    @SuppressWarnings({"unchecked"})
    private Number calculateValue(final Number operandOne, final Number operandTwo,
                                  final CalculatingNumberJmxCollector.Operator operator)
    {
        final ConstantNumberCollector collectorOne = new ConstantNumberCollector(LOGICAL_NAME, operandOne);
        final ConstantNumberCollector collectorTwo = new ConstantNumberCollector(LOGICAL_NAME, operandTwo);
        final CalculatingNumberJmxCollector calculatingNumberJmxCollector =
                new CalculatingNumberJmxCollector(LOGICAL_NAME, collectorOne, collectorTwo, operator);

        return (Number) calculatingNumberJmxCollector.getValue(null);
    }

    private static JmxCollector collectorOfType(final DataType dataType)
    {
        return new DummyJmxCollector(dataType);
    }

    private static final class DummyJmxCollector extends AbstractJmxCollector
    {
        public DummyJmxCollector(final DataType dataType)
        {
            super(LOGICAL_NAME, MonitorType.SCALAR, dataType);
        }

        @Override
        public Object getValue(final MBeanServerConnection mBeanServerConnection)
        {
            throw new UnsupportedOperationException("This stub should not be called");
        }
    }
}