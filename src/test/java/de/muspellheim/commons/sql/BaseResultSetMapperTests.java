/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class BaseResultSetMapperTests {

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
  }

  @Test
  void mapped() throws Exception {
    // When
    ExampleEntity entity;
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(SQL)) {
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        entity = mapper.map(resultSet);
      }
    }

    // Then
    assertEquals(ENTITY_1, entity);
  }

  @Test
  void listMapped() throws Exception {
    // When
    List<ExampleEntity> entities;
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(SQL)) {
        ResultSet resultSet = statement.executeQuery();
        entities = mapper.mapList(resultSet);
      }
    }

    // Then
    assertEquals(Arrays.asList(ENTITY_1, ENTITY_2), entities);
  }

  @Test
  void noMapper() {
    // When
    Throwable exception =
        assertThrows(
            NoSuchElementException.class,
            () -> new ConstructorResultSetMapper<>(BrokenEntity.class));

    // Then
    assertEquals("no column mapper found for: class javax.swing.JPanel", exception.getMessage());
  }
}
