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