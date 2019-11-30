/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * An event send a message to handlers.
 *
 * @param <T> the message type.
 */
public class Event<T> {

  private final List<Consumer<T>> handlers = new CopyOnWriteArrayList<>();

  /**
   * Adds a handler to notify it of sending events.
   *
   * @param handler the handler to add
   */
  public void addHandler(@NonNull Consumer<T> handler) {
    handlers.add(handler);
  }

  /**
   * Removes a handler to not notify it anymore.
   *
   * @param handler the handler to remove
   */
  public void removeHandler(@NonNull Consumer<T> handler) {
    handlers.remove(handler);
  }

  /**
   * Sends a message to all added handlers.
   *
   * @param message the message to send
   */
  public void send(@NonNull T message) {
    handlers.forEach(
        handler -> {
          try {
            handler.accept(message);
          } catch (Exception e) {
            Thread.currentThread()
                .getUncaughtExceptionHandler()
                .uncaughtException(Thread.currentThread(), e);
          }
        });
  }
}
