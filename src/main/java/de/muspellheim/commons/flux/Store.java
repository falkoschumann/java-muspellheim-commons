/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.flux;

/** Base class of a store. */
public abstract class Store<T> {

  private final Dispatcher<T> dispatcher;
  private final String dispatchToken;

  /** Constructs and registers an instance of this store with the given dispatcher. */
  public Store(Dispatcher<T> dispatcher) {
    this.dispatcher = dispatcher;
    dispatchToken = dispatcher.register(this::onDispatch);
  }

  /** Returns the dispatcher this store is registered with. */
  public Dispatcher<T> getDispatcher() {
    return dispatcher;
  }

  /** Returns the dispatch token that the dispatcher recognizes this store by. */
  public String getDispatchToken() {
    return dispatchToken;
  }

  /**
   * Subclasses must override this method. This is how the store receives actions from the
   * dispatcher. All state mutation logic must be done during this method.
   */
  public abstract void onDispatch(T payload);
}
