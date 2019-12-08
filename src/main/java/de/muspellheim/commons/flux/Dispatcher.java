/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.flux;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dispatcher is used to broadcast payloads to registered callbacks. This is different from generic
 * pub-sub systems in two ways:
 *
 * <ul>
 *   <li>Callbacks are not subscribed to particular events. Every payload is dispatched to every
 *       registered callback.
 *   <li>Callbacks can be deferred in whole or part until other callbacks have been executed.
 * </ul>
 */
public class Dispatcher<T> {

  private static final String prefix = "ID_";

  private int lastId = 1;

  private boolean dispatching;

  private Map<String, Consumer<T>> callbacks = Collections.synchronizedMap(new LinkedHashMap<>());

  /** Is this Dispatcher currently dispatching. */
  public boolean isDispatching() {
    return dispatching;
  }

  private void setDispatching(boolean dispatching) {
    this.dispatching = dispatching;
  }

  /** Registers a callback to be invoked with every dispatched payload. */
  public String register(Consumer<T> callback) {
    String id = prefix + lastId++;
    callbacks.put(id, callback);
    return id;
  }

  /** Removes a callback based on its token. */
  public void unregister(String id) {
    if (!callbacks.containsKey(id)) {
      throw new AssertionError(
          "Dispatcher.unregister(...): " + id + " does not map to a registered callback.");
    }

    callbacks.remove(id);
  }

  /** Dispatches a payload to all registered callbacks. */
  public void dispatch(T payload) {
    if (isDispatching()) {
      throw new AssertionError(
          "Dispatch.dispatch(...): Cannot dispatch in the middle of a dispatch.");
    }

    setDispatching(true);
    try {
      callbacks.values().forEach(e -> e.accept(payload));
    } finally {
      setDispatching(false);
    }
  }
}
