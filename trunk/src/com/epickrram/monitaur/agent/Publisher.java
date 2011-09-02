package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.common.domain.MonitorData;

public interface Publisher
{
    <T> void publish(final MonitorData<T> data);
}