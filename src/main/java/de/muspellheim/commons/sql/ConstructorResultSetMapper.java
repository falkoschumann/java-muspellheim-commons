/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.sql.*;

public class ConstructorResultSetMapper<T> extends ResultSetMapper<T> {

    public ConstructorResultSetMapper(Class<T> type) {
        super(type);
    }

    @Override
    public T map(ResultSet resultSet) {
        return null;
    }

}
