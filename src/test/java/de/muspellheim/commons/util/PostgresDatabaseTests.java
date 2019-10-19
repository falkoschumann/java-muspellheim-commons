/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.sql.*;
import javax.sql.*;

import de.muspellheim.commons.sql.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PostgresDatabaseTests {

    @Test
    void connected() throws Exception {
        // Given
        DatabaseConfiguration config = DatabaseConfiguration.of("localhost", 5432, "postgres", "", "java_muspellheim_commons");
        PostgresDatabase database = new PostgresDatabase(config);

        // When
        DataSource dataSource = database.getDataSource();

        // THen
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
    }

}
