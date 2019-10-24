/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.beans.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class ConstructorResultSetMapper<T> extends ResultSetMapper<T> {

    private final Constructor<T> constructor;
    private final Map<String, Integer> mappedParameters;

    public ConstructorResultSetMapper(Class<T> type) {
        super(type);
        constructor = findAnnotatedConstructor();
        mappedParameters = createMappedParameters();
    }

    private Map<String, Integer> createMappedParameters() {
        Map<String, Integer> parameters = new LinkedHashMap<>();
        String[] parameterNames = constructor.getAnnotation(ConstructorProperties.class).value();
        for (int i = 0; i < parameterNames.length; i++) {
            parameters.put(parameterNames[i], i);
        }
        return parameters;
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        try {
            Object[] parameters = new Object[constructor.getParameterCount()];
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = resultSet.getMetaData().getColumnLabel(i);
                String propertyName = propertyNameFor(columnLabel);
                parameters[mappedParameters.get(propertyName)] = mapColumn(resultSet, columnLabel);
            }
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Can not map result set to entity", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> findAnnotatedConstructor() {
        return (Constructor<T>) Arrays.stream(getType().getConstructors())
            .filter(c -> c.getAnnotation(ConstructorProperties.class) != null)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Type must have a constructor annotated with @ConstructorProperties: " + getType()));
    }

}
