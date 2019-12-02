/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class SqlQueryTaskTests {

  private static final ExampleEntity ENTITY_1 =
      new ExampleEntity("Foo", 42, 74.7, true, LocalDateTime.of(2000, 12, 31, 23, 59));
  private static final ExampleEntity ENTITY_2 =
      new ExampleEntity("Bar", 24, 47.4, false, LocalDateTime.of(1999, 8, 2, 12, 0));

  protected DataSource dataSource;
  protected ResultSetMapper<ExampleEntity> mapper;

  @BeforeEach
  void setUp() {
    DatabaseConfiguration config =
        DatabaseConfiguration.of("localhost", 5432, "postgres", "", "java_muspellheim_commons");
    PostgresDatabase database = new PostgresDatabase(config);
    dataSource = database.getDataSource();
    mapper = new ConstructorResultSetMapper<>(ExampleEntity.class);
  }

  @Test
  void initialized() {
    // When
    SqlQueryTask<List<ExampleEntity>> task = createQueryTask();

    // Then
    assertFalse(task.isCancelled(), "is cancelled");
    assertFalse(task.isDone(), "is done");
  }

  @Test
  @Timeout(2)
  void await() throws Exception {
    // Given
    SqlQueryTask<List<ExampleEntity>> task = createQueryTask();
    Thread thread = new Thread(task);

    // When
    thread.start();
    List<ExampleEntity> result = task.get();

    // Then
    assertAll(
        () -> assertEquals(Arrays.asList(ENTITY_1, ENTITY_2), result),
        () -> assertTrue(task.isDone(), "is done"));
  }

  @Test
  @Timeout(2)
  void awaitWithTimeout() throws Exception {
    // Given
    SqlQueryTask<List<ExampleEntity>> task = createQueryTask();
    Thread thread = new Thread(task);

    // When
    thread.start();
    List<ExampleEntity> result = task.get(1, TimeUnit.SECONDS);

    // Then
    assertAll(
        () -> assertEquals(Arrays.asList(ENTITY_1, ENTITY_2), result),
        () -> assertTrue(task.isDone(), "is done"));
  }

  @Test
  @Timeout(2)
  void awaitUntilTimeout() {
    // Given
    SqlQueryTask<List<ExampleEntity>> task = createEndlessQueryTask();
    Thread thread = new Thread(task);

    // When
    thread.start();

    // Then
    assertThrows(TimeoutException.class, () -> task.get(1, TimeUnit.SECONDS));
    assertFalse(task.isDone(), "is done");
  }

  @Test
  @Timeout(2)
  void exceptional() {
    // Given
    SqlQueryTask<List<ExampleEntity>> task =
        new SqlQueryTask<>(
            dataSource,
            "SELECT 1;",
            statement -> {
              throw new IllegalStateException("Foobar");
            });
    Thread thread = new Thread(task);

    // When
    thread.start();

    // Then
    ExecutionException exception = assertThrows(ExecutionException.class, task::get);
    assertAll(
        "cause exception",
        () ->
            assertTrue(
                exception.getCause() instanceof IllegalStateException, "cause exception type"),
        () -> assertEquals("Foobar", exception.getCause().getMessage(), "cause exception message"),
        () -> assertTrue(task.isDone(), "is done"));
  }

  @Test
  @Timeout(2)
  void cancelled() {
    // Given
    // language=PostgreSQL
    SqlQueryTask<List<ExampleEntity>> task = createEndlessQueryTask();
    Thread thread = new Thread(task);

    // When
    thread.start();
    runLater(1, TimeUnit.SECONDS, () -> task.cancel(true));

    // Then
    assertThrows(CancellationException.class, task::get);
    assertAll(
        () -> assertTrue(task.isCancelled(), "is cancelled"),
        () -> assertTrue(task.isDone(), "is done"));
  }

  private SqlQueryTask<List<ExampleEntity>> createQueryTask() {
    // language=PostgreSQL
    String sql =
        "SELECT\n"
            + "    *\n"
            + "FROM\n"
            + "    (VALUES\n"
            + "      ('Foo', 42, 74.7, true, '2000-12-31 23:59'::timestamp),\n"
            + "      ('Bar', 24, 47.4, false, '1999-08-02 12:00'::timestamp)\n"
            + "    ) AS table1(family_name, age, weight, valid_plz_entry, day_of_birth);";
    return new SqlQueryTask<>(
        dataSource,
        sql,
        statement -> {
          ResultSet resultSet = statement.executeQuery();
          return mapper.mapList(resultSet);
        });
  }

  private SqlQueryTask<List<ExampleEntity>> createEndlessQueryTask() {
    // language=PostgreSQL
    String sql =
        "WITH RECURSIVE rec AS (\n"
            + "        SELECT  1 AS n\n"
            + "        UNION ALL\n"
            + "        SELECT n + 1\n"
            + "        FROM rec\n"
            + ")\n"
            + "SELECT n\n"
            + "FROM rec;";

    return new SqlQueryTask<>(
        dataSource,
        sql,
        statement -> {
          ResultSet resultSet = statement.executeQuery();
          return mapper.mapList(resultSet);
        });
  }

  private void runLater(long timeout, TimeUnit unit, Runnable runnable) {
    CompletableFuture.runAsync(
        () -> {
          try {
            unit.sleep(timeout);
            runnable.run();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
  }
}
