/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests, integration tests, acceptance tests and examples.
 *
 * <p>Not enough stuff to separate it.
 */
class EventBusTests {

  private List<String> stringEvents;
  private List<Integer> integerEvents;
  private List<Number> numberEvents;
  private List<Number> doubleEvents;

  private Phaser phaser;
  private Throwable exception;

  @BeforeEach
  void setUp() {
    Thread.setDefaultUncaughtExceptionHandler(this::handleException);

    stringEvents = new ArrayList<>();
    integerEvents = new ArrayList<>();
    numberEvents = new ArrayList<>();
    doubleEvents = new ArrayList<>();

    exception = null;
  }

  @Test
  void publish() throws Exception {
    // Given
    EventBus bus = new EventBus("Testing event bus");
    bus.subscribe(String.class, this::consumeString);
    bus.subscribe(Integer.class, this::consumeInt);

    // When
    phaser = new Phaser(3);
    bus.publish("Foo");
    bus.publish(42);
    bus.publish("Bar");
    phaser.awaitAdvanceInterruptibly(0, 2, TimeUnit.SECONDS);

    // Then
    assertAll(
        () -> assertEquals(Arrays.asList("Foo", "Bar"), stringEvents, "string events"),
        () -> assertEquals(Collections.singletonList(42), integerEvents, "integer events"),
        () -> assertNull(exception, "no exception thrown"));
  }

  @Test
  void handlerThrowsException() throws Exception {
    // Given
    EventBus bus = new EventBus("throwing exception event bus");
    bus.subscribe(
        String.class,
        m -> {
          throw new RuntimeException("Foobar");
        });
    bus.subscribe(String.class, this::consumeString);

    // When
    phaser = new Phaser(2);
    bus.publish("Foo");
    phaser.awaitAdvanceInterruptibly(0, 2, TimeUnit.SECONDS);

    // Then
    assertAll(
        () -> assertEquals(Collections.singletonList("Foo"), stringEvents, "string events"),
        () -> assertEquals("Foobar", exception.getMessage(), "exception thrown"));
  }

  @Test
  void unsubscribe() throws Exception {
    // Given
    EventBus bus = new EventBus("Testing event bus");
    Consumer<String> subscriber = this::consumeString;
    bus.subscribe(String.class, subscriber);

    // When
    phaser = new Phaser(1);
    bus.publish("Foo");
    phaser.awaitAdvanceInterruptibly(0, 2, TimeUnit.SECONDS);
    bus.unsubscribe(subscriber);
    TimeUnit.MILLISECONDS.sleep(200);
    bus.publish("Bar");
    TimeUnit.MILLISECONDS.sleep(200);

    // Then
    assertAll(
        () -> assertEquals(Collections.singletonList("Foo"), stringEvents, "string events"),
        () -> assertNull(exception, "no exception thrown"));
  }

  @Test
  void eventTypeHierarchy() throws Exception {
    // Given
    EventBus bus = new EventBus("Testing event bus");
    Consumer<Integer> intSubscriber = this::consumeInt;
    bus.subscribe(Integer.class, intSubscriber);
    Consumer<Number> numberSubscriber = this::consumeNumber;
    bus.subscribe(Number.class, numberSubscriber);
    Consumer<Number> doubleSubscriber = this::consumeDouble;
    bus.subscribe(Double.class, doubleSubscriber);

    // When
    phaser = new Phaser(4);
    bus.publish(0.815);
    bus.publish(42);
    phaser.awaitAdvanceInterruptibly(0, 2, TimeUnit.SECONDS);
    bus.unsubscribe(Number.class, numberSubscriber);
    bus.unsubscribe(Number.class, doubleSubscriber);
    TimeUnit.MILLISECONDS.sleep(200);
    bus.publish(2.718);
    TimeUnit.MILLISECONDS.sleep(200);
    bus.unsubscribe(Integer.class, intSubscriber);
    TimeUnit.MILLISECONDS.sleep(200);
    bus.publish(7);
    TimeUnit.MILLISECONDS.sleep(200);

    // Then
    assertAll(
        () -> assertEquals(Collections.singletonList(42), integerEvents, "integer events"),
        () -> assertEquals(Arrays.asList(0.815, 42), numberEvents, "number events"),
        () -> assertEquals(Collections.singletonList(0.815), doubleEvents, "double events"),
        () -> assertNull(exception, "no exception thrown"));
  }

  private void consumeString(String event) {
    stringEvents.add(event);
    phaser.arrive();
  }

  private void consumeInt(int event) {
    integerEvents.add(event);
    phaser.arrive();
  }

  private void consumeNumber(Number event) {
    numberEvents.add(event);
    phaser.arrive();
  }

  private void consumeDouble(Number event) {
    doubleEvents.add(event);
    phaser.arrive();
  }

  private void handleException(Thread t, Throwable e) {
    exception = e;
    phaser.arrive();
  }
}
