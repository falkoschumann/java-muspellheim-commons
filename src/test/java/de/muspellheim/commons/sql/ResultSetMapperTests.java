/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("postgres")
public class ResultSetMapperTests {

  private static final String SQL =
      "SELECT\n"
          + "    text_column\n"
          + "FROM\n"
          + "    (VALUES\n"
          + "      ('Foo'),\n"
          + "      ('Bar')\n"
          + "    ) AS table1(text_column);";

  private DataSource dataSource;

  @BeforeEach
  void setUp() {
    DatabaseConfiguration config =
        DatabaseConfiguration.of("localhost", 5432, "postgres", "", "java_muspellheim_commons");
    PostgresDatabase database = new PostgresDatabase(config);
    dataSource = database.getDataSource();
  }

  @Test
  void mapValue() throws Exception {
    // When
    String value;
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(SQL)) {
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        value = ResultSetMapper.mapValue(resultSet, "text_column", String.class);
      }
    }

    // Then
    assertEquals("Foo", value);
  }

  @Test
  void mapValues() throws Exception {
    // When
    List<String> values;
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(SQL)) {
        ResultSet resultSet = statement.executeQuery();
        values = ResultSetMapper.mapValueList(resultSet, String.class);
      }
    }

    // Then
    assertEquals(Arrays.asList("Foo", "Bar"), values);
  }
}
