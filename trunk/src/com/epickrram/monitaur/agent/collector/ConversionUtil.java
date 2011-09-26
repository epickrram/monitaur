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
        return DataType.fromClassname(attributeInfo.getType());
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
        if(isType(value, Integer.class, int.class))
        {
            dataType = DataType.INTEGER;
        }
        else if(isType(value, Long.class, long.class))
        {
            dataType = DataType.LONG;
        }
        else if(isType(value, Float.class, float.class))
        {
            dataType = DataType.FLOAT;
        }
        else if(isType(value, Double.class, double.class))
        {
            dataType = DataType.DOUBLE;
        }
        else
        {
            throw new IllegalArgumentException("Can't convert value " + value + " to Number");
        }
        return dataType;
    }

    private static boolean isType(final Object value, final Class... matchingClasses)
    {
        final Class valueClass = value.getClass();
        boolean matches = false;
        for (Class matchingClass : matchingClasses)
        {
            if(matchingClass.equals(valueClass))
            {
                matches = true;
                break;
            }
        }

        return matches;
    }
}