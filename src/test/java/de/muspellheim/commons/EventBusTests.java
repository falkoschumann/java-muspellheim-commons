/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons;

import java.util.*;
import java.util.function.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests, integration tests, acceptance tests and examples.
 * <p>
 * Not enough stuff to separate it.
 */
class EventBusTests {

    private List<String> stringEvents = new ArrayList<>();
    private List<Integer> integerEvents = new ArrayList<>();
    private List<Number> numberEvents = new ArrayList<>();
    private List<Number> doubleEvents = new ArrayList<>();
    private Throwable exception;

    @Test
    void publish() {
        // Given
        EventBus bus = EventBus.getDefault();
        bus.subscribe(String.class, this::consumeString);
        bus.subscribe(Integer.class, this::consumeInt);

        // When
        bus.publish("Foo");
        bus.publish(42);
        bus.publish("Bar");

        // Then
        assertAll(
            () -> assertEquals(Arrays.asList("Foo", "Bar"), stringEvents, "string events"),
            () -> assertEquals(Collections.singletonList(42), integerEvents, "integer events"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

    @Test
    void handlerThrowsException() {
        // Given
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
        EventBus bus = EventBus.getDefault();
        bus.subscribe(String.class, m -> {
            throw new RuntimeException("Foobar");
        });
        bus.subscribe(String.class, this::consumeString);

        // When
        bus.publish("Foo");

        // Then
        assertAll(
            () -> assertEquals(Collections.singletonList("Foo"), stringEvents, "string events"),
            () -> assertEquals("Foobar", exception.getMessage(), "exception thrown")
        );
    }

    @Test
    void unsubscribe() {
        // Given
        EventBus bus = EventBus.getDefault();
        Consumer<String> subscriber = this::consumeString;
        bus.subscribe(String.class, subscriber);

        // When
        bus.publish("Foo");
        bus.unsubscribe(subscriber);
        bus.publish("Bar");

        // Then
        assertAll(
            () -> assertEquals(Collections.singletonList("Foo"), stringEvents, "string events"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

    @Test
    void eventTypeHierarchy() {
        // Given
        EventBus bus = EventBus.getDefault();
        Consumer<Integer> intSubscriber = this::consumeInt;
        bus.subscribe(Integer.class, intSubscriber);
        Consumer<Number> numberSubscriber = this::consumeNumber;
        bus.subscribe(Number.class, numberSubscriber);
        Consumer<Number> doubleSubscriber = this::consumeDouble;
        bus.subscribe(Double.class, doubleSubscriber);

        // When
        bus.publish(0.815);
        bus.publish(42);
        bus.unsubscribe(Number.class, numberSubscriber);
        bus.unsubscribe(Number.class, doubleSubscriber);
        bus.publish(2.718);
        bus.unsubscribe(Integer.class, intSubscriber);
        bus.publish(7);

        // Then
        assertAll(
            () -> assertEquals(Collections.singletonList(42), integerEvents, "integer events"),
            () -> assertEquals(Arrays.asList(0.815, 42), numberEvents, "number events"),
            () -> assertEquals(Collections.singletonList(0.815), doubleEvents, "double events"),
            () -> assertNull(exception, "no exception thrown")
        );
    }

    private void consumeString(String event) {
        stringEvents.add(event);
    }

    private void consumeInt(int event) {
        integerEvents.add(event);
    }

    private void consumeNumber(Number event) {
        numberEvents.add(event);
    }

    private void consumeDouble(Number event) {
        doubleEvents.add(event);
    }

}
