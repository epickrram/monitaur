package com.epickrram.monitaur.server;

import com.epickrram.monitaur.common.domain.MonitorData;

public interface MonitorDataHandler
{
    void onMonitorData(final MonitorData data);
}
