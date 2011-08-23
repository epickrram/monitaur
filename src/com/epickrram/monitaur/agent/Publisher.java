package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.domain.MonitorData;

public interface Publisher
{
    <T> void publish(final MonitorData<T> data);
}