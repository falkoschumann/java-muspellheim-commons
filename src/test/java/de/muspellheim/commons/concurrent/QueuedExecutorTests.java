/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.concurrent;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

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
            } catch (InterruptedException ignored) {
            }
            values.add(value);
        }

    }

}
