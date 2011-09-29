package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.domain.MonitorData;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

public final class MonitorDataStore implements MonitorDataHandler
{
    private final ConcurrentMap<String, List<MonitorData>> historicDataByLogicalName =
            new ConcurrentHashMap<String, List<MonitorData>>();

    private final int maxHistorySize;

    public MonitorDataStore(final int maxHistorySize)
    {
        this.maxHistorySize = maxHistorySize;
    }

    @Override
    public void onMonitorData(final MonitorData data)
    {
        final String logicalName = data.getLogicalName();
        final List<MonitorData> dataList = getDataListForLogicalName(logicalName);

        dataList.add(data);

        while(dataList.size() > maxHistorySize)
        {
            dataList.remove(0);
        }
    }

    public List<MonitorData> getMonitorDataByLogicalName(final String logicalName)
    {
        return historicDataByLogicalName.containsKey(logicalName) ?
                unmodifiableList(historicDataByLogicalName.get(logicalName)) :
                Collections.<MonitorData>emptyList();
    }

    public Set<String> getLogicalNames()
    {
        return unmodifiableSet(historicDataByLogicalName.keySet());
    }

    private List<MonitorData> getDataListForLogicalName(final String logicalName)
    {
        List<MonitorData> dataList;
        if(!historicDataByLogicalName.containsKey(logicalName))
        {
            dataList = new CopyOnWriteArrayList<MonitorData>();
            final List<MonitorData> existing = historicDataByLogicalName.putIfAbsent(logicalName, dataList);
            if(existing != null)
            {
                dataList = existing;
            }
        }
        else
        {
            dataList = historicDataByLogicalName.get(logicalName);
        }
        return dataList;
    }
}