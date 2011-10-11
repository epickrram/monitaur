package com.epickrram.monitaur.common;

public interface MessagingHelper
{
    public <T> T createPublisher(final Class<T> publisherClass);
    public <T> void registerSubscriber(final Class<T> subscriberClass, final T implementation);
    public void start();
    public void stop();
}
