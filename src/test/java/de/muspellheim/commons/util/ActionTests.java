/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
        () -> assertNull(exception, "no exception thrown"));
  }

  @Test
  void handlerThrowsException() {
    // Given
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> exception = e);
    Action action = new Action();
    action.addHandler(
        () -> {
          throw new RuntimeException("Foobar");
        });
    action.addHandler(() -> triggered = true);

    // When
    action.trigger();

    // Then
    assertAll(
        () -> assertTrue(triggered, "action triggered"),
        () -> assertEquals("Foobar", exception.getMessage(), "exception thrown"));
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
        () -> assertNull(exception, "no exception thrown"));
  }
}
