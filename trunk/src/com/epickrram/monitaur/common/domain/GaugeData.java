package com.epickrram.monitaur.common.domain;

public final class GaugeData
{
    private final Number minimum;
    private final Number maximum;
    private final Number currentValue;

    public GaugeData(final Number minimum, final Number maximum, final Number currentValue)
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
        return "GaugeData{" +
                "minimum=" + minimum +
                ", maximum=" + maximum +
                ", currentValue=" + currentValue +
                ", percentage=" + ((currentValue.floatValue() / (maximum.floatValue() - minimum.floatValue())) * 100) +
                '}';
    }


}