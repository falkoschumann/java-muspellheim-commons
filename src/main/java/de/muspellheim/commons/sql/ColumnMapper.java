/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.sql.*;

@FunctionalInterface
public interface ColumnMapper<T> {

    T map(ResultSet resultSet, String columnLabel) throws SQLException;

}
