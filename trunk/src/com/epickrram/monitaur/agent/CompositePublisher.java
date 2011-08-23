package com.epickrram.monitaur.agent;

import com.epickrram.monitaur.agent.domain.MonitorData;

public final class CompositePublisher implements Publisher
{
    private final Publisher[] publishers;

    public CompositePublisher(final Publisher... publishers)
    {
        this.publishers = publishers;
    }

    @Override
    public <T> void publish(final MonitorData<T> data)
    {
        for (Publisher publisher : publishers)
        {
            publisher.publish(data);
        }
    }
}
