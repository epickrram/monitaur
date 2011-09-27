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