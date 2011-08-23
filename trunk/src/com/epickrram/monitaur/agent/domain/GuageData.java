package com.epickrram.monitaur.agent.domain;

public final class GuageData
{
    private final Number minimum;
    private final Number maximum;
    private final Number currentValue;

    public GuageData(final Number minimum, final Number maximum, final Number currentValue)
    {
        this.minimum = minimum;
        this.maximum = maximum;
        this.currentValue = currentValue;
    }

    public Number getMinimum()
    {
        return minimum;
    }

    public Number getMaximum()
    {
        return maximum;
    }

    public Number getCurrentValue()
    {
        return currentValue;
    }

    public float getPercentage()
    {
        return ((currentValue.floatValue() / (maximum.floatValue() - minimum.floatValue())) * 100);
    }

    @Override
    public String toString()
    {
        return "GuageData{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                ", currentValue=" + currentValue +
                ", percentage=" + ((currentValue.floatValue() / (maximum.floatValue() - minimum.floatValue())) * 100) +
                '}';
    }


}