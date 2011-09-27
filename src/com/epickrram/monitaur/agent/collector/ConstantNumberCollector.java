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

import javax.management.MBeanServerConnection;

import static com.epickrram.monitaur.agent.collector.ConversionUtil.determineNumberDataType;

public final class ConstantNumberCollector<T extends Number> extends AbstractCollector<MBeanServerConnection> implements JmxCollector
{
    private final T value;
    private final DataType dataType;

    public ConstantNumberCollector(final String logicalName, final T value)
    {
        super(logicalName, MonitorType.SCALAR, ConversionUtil.determineNumberDataType(value));
        this.value = value;
        this.dataType = determineNumberDataType(value);
    }

    @Override
    public T getValue(final MBeanServerConnection provider)
    {
        return value;
    }

    @Override
    public DataType getType()
    {
        return dataType;
    }
}
