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


import com.epickrram.monitaur.common.domain.MonitorType;

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.add;
import static com.epickrram.monitaur.agent.collector.ConversionUtil.subtract;

public final class CalculatingNumberJmxCollector extends AbstractJmxCollector implements JmxCollector
{
    private final JmxCollector operandOne;
    private final JmxCollector operandTwo;
    private final Operator operator;

    public CalculatingNumberJmxCollector(final String logicalName,
                                         final JmxCollector operandOne, final JmxCollector operandTwo,
                                         final Operator operator)
    {
        super(logicalName, MonitorType.SCALAR, operandOne.getType());
        this.operandOne = operandOne;
        this.operandTwo = operandTwo;
        this.operator = operator;
        validate();
    }

    private void validate()
    {
        if(!(operandOne.getType().isNumber() && operandTwo.getType().isNumber()))
        {
            throw new IllegalArgumentException("Both operands must be collectors of java.lang.Number");
        }
    }

    @Override
    public Object getValue(final MBeanServerConnection mBeanServerConnection)
    {
        final Number value;
        final Object valueOne = operandOne.getValue(mBeanServerConnection);
        final Object valueTwo = operandTwo.getValue(mBeanServerConnection);
        switch(operator)
        {
            case ADD:
                    value = add(valueOne, valueTwo);
                break;
            case SUBTRACT:
                    value = subtract(valueOne, valueTwo);
                break;
            default:
                throw new IllegalArgumentException("Unknown operator type: " + operator);
        }
        return value;
    }

    public enum Operator
    {
        ADD,
        SUBTRACT
    }
}