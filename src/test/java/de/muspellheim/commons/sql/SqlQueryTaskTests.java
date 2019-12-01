/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class SqlQueryTaskTests {

  static final String SQL =
      "SELECT\n"
          + "    *\n"
          + "FROM\n"
          + "    (VALUES\n"
          + "      ('Foo', 42, 74.7, true, '2000-12-31 23:59'::timestamp),\n"
          + "      ('Bar', 24, 47.4, false, '1999-08-02 12:00'::timestamp)\n"
          + "    ) AS table1(family_name, age, weight, valid_plz_entry, day_of_birth);";

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
  @Timeout(2)
  void await() throws Exception {
    // Given
    SqlQueryTask<List<ExampleEntity>> task =
        new SqlQueryTask<>(
            dataSource,
            SQL,
            statement -> {
              ResultSet resultSet = statement.executeQuery();
              return mapper.mapList(resultSet);
            });
    Thread thread = new Thread(task);

    // When
    thread.start();
    List<ExampleEntity> result = task.get();

    // Then
    assertEquals(Arrays.asList(ENTITY_1, ENTITY_2), result);
  }

  @Test
  @Timeout(2)
  void awaitUntilTimeout() throws Exception {
    // Given
    SqlQueryTask<List<ExampleEntity>> task =
        new SqlQueryTask<>(
            dataSource,
            SQL,
            statement -> {
              ResultSet resultSet = statement.executeQuery();
              return mapper.mapList(resultSet);
            });
    Thread thread = new Thread(task);

    // When
    thread.start();
    List<ExampleEntity> result = task.get(1, TimeUnit.SECONDS);

    // Then
    assertEquals(Arrays.asList(ENTITY_1, ENTITY_2), result);
  }
}
