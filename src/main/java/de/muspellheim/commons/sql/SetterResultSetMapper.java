/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.beans.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

public class SetterResultSetMapper<T> extends ResultSetMapper<T> {

    private final Map<String, Method> setters;

    public SetterResultSetMapper(Class<T> type) {
        super(type);
        setters = createSetters();
    }

    private Map<String, Method> createSetters() {
        BeanInfo beanInfo = getBeanInfo();
        return Arrays.stream(beanInfo.getPropertyDescriptors())
            .filter(p -> p.getWriteMethod() != null)
            .collect(Collectors.toMap(FeatureDescriptor::getName, PropertyDescriptor::getWriteMethod));
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        try {
            T entity = getType().getDeclaredConstructor().newInstance();
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel = resultSet.getMetaData().getColumnLabel(i);
                String propertyName = propertyNameFor(columnLabel);
                Method setter = setters.get(propertyName);
                Object value = mapColumn(resultSet, columnLabel);
                setter.invoke(entity, value);
            }
            return entity;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Can not map result set to entity", e);
        }
    }

}
