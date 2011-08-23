package com.epickrram.monitaur.agent.collector;

@SuppressWarnings({"UnnecessaryBoxing"})
public final class ConversionUtil
{
    private ConversionUtil()
    {
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
}
