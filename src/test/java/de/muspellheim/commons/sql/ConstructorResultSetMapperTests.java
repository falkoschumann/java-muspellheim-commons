/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.sql.*;
import java.time.*;
import javax.sql.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("postgres")
class ConstructorResultSetMapperTests {

    private static final String SQL = ""
        + "SELECT\n"
        + "    *\n"
        + "FROM\n"
        + "    (VALUES\n"
        + "      ('Foo', 42, 74.7, true, '2000-12-31 23:59'::timestamp)"
        + "    ) AS table1(family_name, age, weight, valid_plz_entry, day_of_birth);";

    private static final ExampleEntity ENTITY = new ExampleEntity("Foo", 42, 74.7, true, LocalDateTime.of(2000, 12, 31, 23, 59));

    @Test
    void mapped() throws Exception {
        // Given
        DatabaseConfiguration config = DatabaseConfiguration.of("localhost", 5432, "postgres", "", "java_muspellheim_commons");
        PostgresDatabase database = new PostgresDatabase(config);
        DataSource dataSource = database.getDataSource();
        ResultSetMapper<ExampleEntity> mapper = new ConstructorResultSetMapper<>(ExampleEntity.class);

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
        assertEquals(ENTITY, entity);
    }

    @Test
    void noMapper() {
        // When
        Throwable exception = assertThrows(IllegalStateException.class, () -> new ConstructorResultSetMapper<>(BrokenEntity.class));

        // Then
        assertEquals("no column mapper found: class javax.swing.JPanel", exception.getMessage());
    }

}
