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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("postgres")
class ConstructorResultSetMapperTests extends BaseResultSetMapperTests {

  @BeforeEach
  void setUp() {
    super.setUp();
    mapper = new ConstructorResultSetMapper<>(ExampleEntity.class);
  }

  @Test
  void missingConstructorProperties() throws Exception {
    // Given
    String sql =
        "SELECT\n"
            + "    *\n"
            + "FROM\n"
            + "    (VALUES\n"
            + "      ('Foo', 42),\n"
            + "      ('Bar', 24)\n"
            + "    ) AS table1(text, number);";
    ResultSetMapper<MissingConstructorPropertiesEntity> mapper =
        new ConstructorResultSetMapper<>(MissingConstructorPropertiesEntity.class);

    // When
    ResultSet resultSet;
    Throwable exception;
    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        resultSet = statement.executeQuery();
        resultSet.next();
        exception = assertThrows(IllegalStateException.class, () -> mapper.map(resultSet));
      }
    }

    // Then
    assertEquals("no mapped property found for column: number", exception.getMessage());
  }
}
