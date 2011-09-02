package com.epickrram.monitaur.agent.collector;

import com.epickrram.monitaur.common.domain.DataType;

import javax.management.MBeanAttributeInfo;

@SuppressWarnings({"UnnecessaryBoxing"})
public final class ConversionUtil
{
    private ConversionUtil()
    {
    }

    public static DataType determineDataType(final MBeanAttributeInfo attributeInfo)
    {
        return null;
    }

    public static Number add(final Object operandOne, final Object operandTwo)
    {
        final Number valueOne = toNumber(operandOne);
        final Number valueTwo = toNumber(operandTwo);

        if(valueOne instanceof Long && valueTwo instanceof Long)
        {
            return Long.valueOf(valueOne.longValue() + valueTwo.longValue());
        }
        return Float.valueOf(valueOne.floatValue() + valueTwo.floatValue());
    }

    public static Number subtract(final Object operandOne, final Object operandTwo)
    {
        final Number valueOne = toNumber(operandOne);
        final Number valueTwo = toNumber(operandTwo);

        if(valueOne instanceof Long && valueTwo instanceof Long)
        {
            return Long.valueOf(valueOne.longValue() - valueTwo.longValue());
        }
        return Float.valueOf(valueOne.floatValue() - valueTwo.floatValue());
    }

    public static Number toNumber(final Object rawValue)
    {
        final String value = String.valueOf(rawValue);
        try
        {
            if (value.indexOf('.') > -1)
            {
                return Float.valueOf(value);
            }
            else
            {
                return Long.valueOf(value);
            }
        }
        catch(NumberFormatException e)
        {
            return Long.valueOf(-1);
        }
    }

    public static <T extends Number> DataType determineNumberDataType(final T value)
    {
        final DataType dataType;
        if(Integer.class.equals(value.getClass()))
        {
            dataType = DataType.INTEGER;
        }
        else if(Long.class.equals(value.getClass()))
        {
            dataType = DataType.LONG;
        }
        else if(Float.class.equals(value.getClass()))
        {
            dataType = DataType.FLOAT;
        }
        else if(Double.class.equals(value.getClass()))
        {
            dataType = DataType.DOUBLE;
        }
        else
        {
            dataType = DataType.OBJECT;
        }
        return dataType;
    }
}
