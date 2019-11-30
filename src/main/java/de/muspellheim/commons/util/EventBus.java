/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * Simple event bus implementation.
 *
 * <p>Subscribe and publish events. Events are published in channels distinguished by event type.
 * Channels can be grouped using an event type hierarchy.
 *
 * <p>You can use the default event bus instance {@link #getDefault}, which is a singleton or you
 * can create one or multiple instances of {@link EventBus}.
 *
 * <p>Example:
 *
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

  private static final EventBus defaultEventBus = new EventBus("Default Event Bus");

  private final ConcurrentMap<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();
  private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

  private final String name;

  /**
   * Creates an event bus with given name.
   *
   * @param name the name for the new event bus
   */
  public EventBus(String name) {
    this.name = name;
    EventDeliveryTask task = new EventDeliveryTask();
    Thread t = new Thread(task, name);
    t.setDaemon(true);
    t.start();
  }

  /**
   * Get the default event bus, can be used as application wide singleton.
   *
   * @return the only instance of the default event bus
   */
  public static EventBus getDefault() {
    return defaultEventBus;
  }

  /**
   * Obtains the name of this event bus.
   *
   * @return the name of this event bus
   */
  public String getName() {
    return name;
  }

  /**
   * Subscribe to an event type.
   *
   * @param eventType the event type, can be a super class of all events to subscribe
   * @param subscriber the subscriber which will consume the events
   * @param <T> the event type class
   */
  public <T> void subscribe(
      @NonNull Class<? extends T> eventType, @NonNull Consumer<T> subscriber) {
    List<Consumer<?>> s = subscribers.computeIfAbsent(eventType, t -> new CopyOnWriteArrayList<>());
    s.add(subscriber);
  }

  /**
   * Unsubscribe from all event types.
   *
   * @param subscriber the subscriber to unsubscribe
   */
  public void unsubscribe(@NonNull Consumer<?> subscriber) {
    subscribers.values().forEach(subscribers -> subscribers.remove(subscriber));
  }

  /**
   * Unsubscribe from an event type.
   *
   * @param eventType the event type, can be a super class of all events to unsubscribe
   * @param subscriber the subscriber to unsubscribe
   * @param <T> the event type class
   */
  public <T> void unsubscribe(
      @NonNull Class<? extends T> eventType, @NonNull Consumer<T> subscriber) {
    subscribers.keySet().stream()
        .filter(eventType::isAssignableFrom)
        .map(subscribers::get)
        .forEach(subscribers -> subscribers.remove(subscriber));
  }

  /**
   * Publish an event to all subscribers.
   *
   * <p>The event type is the class of <code>event</code>. The event is published to all consumers
   * which subscribed to this event type or any super class.
   *
   * @param event the event
   */
  public void publish(@NonNull Object event) {
    try {
      queue.put(event);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  private class EventDeliveryTask implements Runnable {

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Object event = queue.take();
          Class<?> eventType = event.getClass();
          subscribers.keySet().stream()
              .filter(type -> type.isAssignableFrom(eventType))
              .flatMap(type -> subscribers.get(type).stream())
              .forEach(subscriber -> publish(event, subscriber));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void publish(Object event, Consumer subscriber) {
      try {
        subscriber.accept(event);
      } catch (Exception e) {
        Thread.currentThread()
            .getUncaughtExceptionHandler()
            .uncaughtException(Thread.currentThread(), e);
      }
    }
  }
}
