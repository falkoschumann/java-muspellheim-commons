/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Simple event bus implementation.
 * <p>
 * Subscribe and publish events. Events are published in channels distinguished by event type. Channels can be grouped
 * using an event type hierarchy.
 * <p>
 * You can use the default event bus instance {@link #getDefault}, which is a singleton or you can create one or multiple
 * instances of {@link EventBus}.
 * <p>
 * Example:
 * <pre><code>
 * EventBus bus = EventBus.getDefault();
 * bus.subscribe(String.class, e -&gt; System.out.println(e));
 * bus.subscribe(Integer.class, e -&gt; System.out.println(e));
 *
 * bus.publish("Foo");
 * bus.publish(42);
 * </code></pre>
 */
public class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    private final ConcurrentMap<Class<?>, List<Consumer>> typedSubscribers = new ConcurrentHashMap<>();

    /**
     * Get the default event bus, can be used as application wide singleton.
     *
     * @return the only instance of the default event bus.
     */
    public static EventBus getDefault() {
        return INSTANCE;
    }

    /**
     * Subscribe to an event type
     *
     * @param eventType  the event type, can be a super class of all events to subscribe.
     * @param subscriber the subscriber which will consume the events.
     * @param <T>        the event type class.
     */
    public <T> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");

        List<Consumer> subscribers = typedSubscribers.computeIfAbsent(eventType, t -> new CopyOnWriteArrayList<>());
        subscribers.add(subscriber);
    }

    /**
     * Unsubscribe from all event types.
     *
     * @param subscriber the subscriber to unsubscribe.
     */
    public void unsubscribe(Consumer<?> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        typedSubscribers.values().forEach(subscribers -> subscribers.remove(subscriber));
    }

    /**
     * Unsubscribe from an event type.
     *
     * @param eventType  the event type, can be a super class of all events to unsubscribe.
     * @param subscriber the subscriber to unsubscribe.
     * @param <T>        the event type class.
     */
    public <T> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");

        typedSubscribers.keySet().stream()
            .filter(eventType::isAssignableFrom)
            .map(typedSubscribers::get)
            .forEach(subscribers -> subscribers.remove(subscriber));
    }

    /**
     * Publish an event to all subscribers.
     * <p>
     * The event type is the class of <code>event</code>. The event is published to all consumers which subscribed to
     * this event type or any super class.
     *
     * @param event the event.
     */
    public void publish(Object event) {
        Objects.requireNonNull(event, "event");

        Class<?> eventType = event.getClass();
        typedSubscribers.keySet().stream()
            .filter(type -> type.isAssignableFrom(eventType))
            .flatMap(type -> typedSubscribers.get(type).stream())
            .forEach(subscriber -> publish(event, subscriber));
    }

    @SuppressWarnings("unchecked")
    private static void publish(Object event, Consumer subscriber) {
        try {
            subscriber.accept(event);
        } catch (Exception e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

}
