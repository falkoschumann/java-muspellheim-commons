/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.sql.*;

/**
 * Map a column of result set to a value.
 *
 * @param <T> value type.
 */
@FunctionalInterface
public interface ColumnMapper<T> {

    /**
     * Map column to value.
     *
     * @param resultSet   a result set
     * @param columnLabel column label to map.
     * @return the column value.
     * @throws SQLException if an error occurred.
     */
    T map(ResultSet resultSet, String columnLabel) throws SQLException;

}
