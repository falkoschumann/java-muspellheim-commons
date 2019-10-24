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

/**
 * Map a result set to a data object using setters.
 * <p>
 * This class ist stateless so it an instance can be keep for performance.
 *
 * @param <T> the entity type.
 * @see ResultSetMapper
 */
public class SetterResultSetMapper<T> extends ResultSetMapper<T> {

    private final Map<String, Method> mappedSetters;

    public SetterResultSetMapper(Class<T> type) {
        super(type);
        mappedSetters = createMappedSetters();
    }

    private Map<String, Method> createMappedSetters() {
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
                Method setter = mappedSetters.get(propertyName);
                Object value = mapColumn(resultSet, columnLabel);
                setter.invoke(entity, value);
            }
            return entity;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Can not map result set to entity", e);
        }
    }

}
