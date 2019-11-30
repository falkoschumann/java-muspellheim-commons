/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muspellheim.commons.domain.AuthorizationException;
import de.muspellheim.commons.domain.ConnectionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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

  @Test
  void connectionException() {
    // Given
    DatabaseConfiguration config =
        DatabaseConfiguration.of("foobar", 5432, "postgres", "", "java_muspellheim_commons");
    PostgresDatabase database = new PostgresDatabase(config);
    DataSource dataSource = database.getDataSource();

    // When
    Executable executable =
        () -> {
          try {
            dataSource.getConnection();
          } catch (SQLException e) {
            new SqlRepository().checkForRepositoryException(e);
          }
        };

    // Then
    assertThrows(ConnectionException.class, executable);
  }

  @Test
  void authorizationException() {
    // Given
    DatabaseConfiguration config =
        DatabaseConfiguration.of("localhost", 5432, "foobar", "", "java_muspellheim_commons");
    PostgresDatabase database = new PostgresDatabase(config);
    DataSource dataSource = database.getDataSource();

    // When
    Executable executable =
        () -> {
          try {
            dataSource.getConnection();
          } catch (SQLException e) {
            new SqlRepository().checkForRepositoryException(e);
          }
        };

    // Then
    assertThrows(AuthorizationException.class, executable);
  }
}
