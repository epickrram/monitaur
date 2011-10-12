/*
Copyright [2011] [Mark price]

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

public abstract class AbstractCollector<ProviderType> implements Collector<ProviderType>
{
    private final String logicalName;
    private final MonitorType monitorType;
    private final DataType dataType;

    public AbstractCollector(final String logicalName, final MonitorType monitorType, final DataType dataType)
    {
        this.logicalName = logicalName;
        this.monitorType = monitorType;
        this.dataType = dataType;
    }

    @Override
    public String getLogicalName()
    {
        return logicalName;
    }

    @Override
    public MonitorType getMonitorType()
    {
        return monitorType;
    }

    public DataType getDataType()
    {
        return dataType;
    }
}