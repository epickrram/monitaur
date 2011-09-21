package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.common.domain.MonitorData;

public interface Publisher
{
    void publish(final MonitorData data);
}