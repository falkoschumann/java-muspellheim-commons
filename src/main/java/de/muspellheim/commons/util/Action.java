/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.*;
import java.util.concurrent.*;

/**
 * An action is an event without message.
 */
public class Action {

    private final List<Runnable> handlers = new CopyOnWriteArrayList<>();

    /**
     * Adds a handler to notify it of sending events.
     *
     * @param handler the handler to add.
     */
    public void addHandler(Runnable handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.add(handler);
    }

    /**
     * Removes a handler to not notify it anymore.
     *
     * @param handler the handler to remove.
     */
    public void removeHandler(Runnable handler) {
        Objects.requireNonNull(handler, "handler");
        handlers.remove(handler);
    }

    /**
     * Trigger the action to all added handlers.
     */
    public void trigger() {
        handlers.forEach(handler -> {
            try {
                handler.run();
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        });
    }

}
