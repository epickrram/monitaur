package com.epickrram.monitaur.common;

import com.epickrram.freewheel.messaging.MessagingService;
import com.epickrram.freewheel.messaging.Receiver;
import com.epickrram.freewheel.protocol.CodeBook;
import com.epickrram.freewheel.remoting.ClassNameTopicIdGenerator;
import com.epickrram.freewheel.remoting.PublisherFactory;
import com.epickrram.freewheel.remoting.SubscriberFactory;

public final class FreewheelMessagingHelper implements MessagingHelper
{
    private final MessagingService messagingService;
    private final PublisherFactory publisherFactory;
    private final SubscriberFactory subscriberFactory;
    private final ClassNameTopicIdGenerator topicIdGenerator;

    public FreewheelMessagingHelper(final MessagingService messagingService, final CodeBook codeBook)
    {
        this.messagingService = messagingService;
        topicIdGenerator = new ClassNameTopicIdGenerator();
        publisherFactory = new PublisherFactory(messagingService, topicIdGenerator, codeBook);
        subscriberFactory = new SubscriberFactory();
    }

    @Override
    public <T> T createPublisher(final Class<T> publisherClass)
    {
        return publisherFactory.createPublisher(publisherClass);
    }

    @Override
    public <T> void registerSubscriber(final Class<T> subscriberClass, final T implementation)
    {
        final Receiver receiver = subscriberFactory.createReceiver(subscriberClass, implementation);
        messagingService.registerReceiver(topicIdGenerator.getTopicId(subscriberClass), receiver);
    }

    @Override
    public void start()
    {
        messagingService.start();
    }

    @Override
    public void stop()
    {
        messagingService.shutdown();
    }
}