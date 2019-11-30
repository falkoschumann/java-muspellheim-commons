/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueuedExecutorTests {

  private List<Integer> values;

  @BeforeEach
  void setUp() {
    values = Collections.synchronizedList(new ArrayList<>());
  }

  @Test
  void queued() throws Exception {
    // Given
    QueuedExecutor executor = new QueuedExecutor("Test");

    // When
    executor.execute(new Task(1, Duration.ofMillis(500)));
    executor.execute(new Task(2, Duration.ofMillis(300)));
    executor.execute(new Task(3, Duration.ofMillis(100)));
    TimeUnit.MILLISECONDS.sleep(1200);

    // Then
    assertEquals(Arrays.asList(1, 2, 3), values);
  }

  private class Task implements Runnable {

    private final int value;
    private final Duration timeout;

    Task(int value, Duration timeout) {
      this.value = value;
      this.timeout = timeout;
    }

    @Override
    public void run() {
      try {
        TimeUnit.MILLISECONDS.sleep(timeout.toMillis());
        values.add(value);
      } catch (InterruptedException e) {
        // nothing to do, simply end
      }
    }
  }
}
