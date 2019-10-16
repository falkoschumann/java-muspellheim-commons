/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ActionTests {

    private boolean triggered;
    private Throwable exception;

    @Test
    void actionTriggered() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Action action = new Action();
        action.addHandler(() -> triggered = true);

        // When
        action.trigger();

        // Then
        assertAll(
            () -> assertTrue(triggered, "action triggered"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

    @Test
    void handlerThrowsException() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Action action = new Action();
        action.addHandler(() -> {
            throw new RuntimeException("Foobar");
        });
        action.addHandler(() -> triggered = true);

        // When
        action.trigger();

        // Then
        assertAll(
            () -> assertTrue(triggered, "action triggered"),
            () -> assertEquals("Foobar", exception.getMessage(), "exception thrown")
        );
    }

    @Test
    void handlerRemoved() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Action action = new Action();
        Runnable handler = () -> triggered = true;
        action.addHandler(handler);

        // When
        action.removeHandler(handler);
        action.trigger();

        // Then
        assertAll(
            () -> assertFalse(triggered, "no action triggered"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

}
