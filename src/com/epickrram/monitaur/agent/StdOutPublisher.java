package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.domain.MonitorData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StdOutPublisher implements Publisher
{
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public <T> void publish(final MonitorData<T> data)
    {
        System.out.println(formatter.format(new Date(data.getTimestamp())) + ", type: " + data.getMonitorType() + " - " + data.getLogicalName() + "=" + data.getDatumAsString());
    }
}