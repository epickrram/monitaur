package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.common.domain.MonitorData;

public final class CompositePublisher implements Publisher
{
    private final Publisher[] publishers;

    public CompositePublisher(final Publisher... publishers)
    {
        this.publishers = publishers;
    }

    @Override
    public void publish(final MonitorData data)
    {
        for (Publisher publisher : publishers)
        {
            publisher.publish(data);
        }
    }
}
