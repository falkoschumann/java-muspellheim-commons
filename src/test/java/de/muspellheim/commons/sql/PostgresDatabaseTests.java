/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("postgres")
class PostgresDatabaseTests {

  @Test
  void connected() throws Exception {
    // Given
    DatabaseConfiguration config =
        DatabaseConfiguration.of("localhost", 5432, "postgres", "", "java_muspellheim_commons");
    PostgresDatabase database = new PostgresDatabase(config);

    // When
    DataSource dataSource = database.getDataSource();
    boolean found;
    try (Connection connection = dataSource.getConnection()) {
      String sql = "SELECT * FROM (VALUES ('Foo'), ('Bar')) AS table1;";
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        ResultSet resultSet = statement.executeQuery();
        found = resultSet.next();
      }
    }
    // Then
    assertTrue(found, "Result found");
  }
}
