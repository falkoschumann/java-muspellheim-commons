/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.util.function.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class EventTests {

    private String message;
    private Throwable exception;

    @Test
    void messageSend() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Event<String> event = new Event<>();
        event.addHandler(m -> message = m);

        // When
        event.send("Foo");

        // Then
        assertAll(
            () -> assertEquals("Foo", message, "message send"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

    @Test
    void handlerThrowsException() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Event<String> event = new Event<>();
        event.addHandler(m -> {
            throw new RuntimeException("Foobar");
        });
        event.addHandler(m -> message = m);

        // When
        event.send("Foo");

        // Then
        assertAll(
            () -> assertEquals("Foo", message, "message send"),
            () -> assertEquals("Foobar", exception.getMessage(), "exception thrown")
        );
    }

    @Test
    void handlerRemoved() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        Event<String> event = new Event<>();
        Consumer<String> handler = m -> message = m;
        event.addHandler(handler);

        // When
        event.removeHandler(handler);
        event.send("Foo");

        // Then
        assertAll(
            () -> assertNull(message, "no message send"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

}
