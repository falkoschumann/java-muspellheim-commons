/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.beans.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public abstract class ResultSetMapper<T> {

    private static final Map<Class<?>, ColumnMapper> MAPPINGS = new HashMap<>();

    static {
        MAPPINGS.put(String.class, ResultSet::getString);

        MAPPINGS.put(Long.class, ResultSetMapper::getLong);
        MAPPINGS.put(long.class, ResultSetMapper::getLong);
        MAPPINGS.put(Integer.class, ResultSetMapper::getInteger);
        MAPPINGS.put(int.class, ResultSetMapper::getInteger);
        MAPPINGS.put(Short.class, ResultSetMapper::getShort);
        MAPPINGS.put(short.class, ResultSetMapper::getShort);
        MAPPINGS.put(Byte.class, ResultSetMapper::getByte);
        MAPPINGS.put(byte.class, ResultSetMapper::getByte);

        MAPPINGS.put(Double.class, ResultSetMapper::getDouble);
        MAPPINGS.put(double.class, ResultSetMapper::getDouble);
        MAPPINGS.put(Float.class, ResultSetMapper::getFloat);
        MAPPINGS.put(float.class, ResultSetMapper::getFloat);

        MAPPINGS.put(Boolean.class, ResultSetMapper::getBoolean);
        MAPPINGS.put(boolean.class, ResultSetMapper::getBoolean);

        MAPPINGS.put(byte[].class, ResultSet::getBytes);

        MAPPINGS.put(LocalDate.class, (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalDate.class));
        MAPPINGS.put(LocalTime.class, (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalTime.class));
        MAPPINGS.put(LocalDateTime.class, (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalDateTime.class));
        MAPPINGS.put(Instant.class, (resultSet, columnLabel) -> resultSet.getObject(columnLabel, Instant.class));
    }

    private final Class<T> type;
    private final Map<String, String> mappedColumns;
    private final Map<String, ColumnMapper> mappings;

    public ResultSetMapper(Class<T> type) {
        this.type = type;
        mappedColumns = createMappedColumns();
        mappings = createMappings();
    }

    public Class<T> getType() {
        return type;
    }

    public static <T> void registerMapping(Class<T> type, ColumnMapper<T> columnMapper) {
        MAPPINGS.put(type, columnMapper);
    }

    public static Double getDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        double v = resultSet.getDouble(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Float getFloat(ResultSet resultSet, String columnLabel) throws SQLException {
        float v = resultSet.getFloat(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Long getLong(ResultSet resultSet, String columnLabel) throws SQLException {
        long v = resultSet.getLong(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Integer getInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        int v = resultSet.getInt(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Short getShort(ResultSet resultSet, String columnLabel) throws SQLException {
        short v = resultSet.getShort(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Byte getByte(ResultSet resultSet, String columnLabel) throws SQLException {
        byte v = resultSet.getByte(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public static Boolean getBoolean(ResultSet resultSet, String columnLabel) throws SQLException {
        boolean v = resultSet.getBoolean(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    public abstract T map(ResultSet resultSet) throws SQLException;

    protected String propertyNameFor(String columnLabel) {
        return mappedColumns.get(columnLabel);
    }

    protected Object mapColumn(ResultSet resultSet, String columnLabel) throws SQLException {
        ColumnMapper mapper = mappings.get(columnLabel);
        return mapper.map(resultSet, columnLabel);
    }

    protected BeanInfo getBeanInfo() {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Can not create name mapping", e);
        }
        return beanInfo;
    }

    private Map<String, String> createMappedColumns() {
        BeanInfo beanInfo = getBeanInfo();
        return Arrays.stream(beanInfo.getPropertyDescriptors())
            .filter(p -> !p.getName().equals("class"))
            .collect(Collectors.toMap(ResultSetMapper::columnLabel, FeatureDescriptor::getName));
    }

    private Map<String, ColumnMapper> createMappings() {
        BeanInfo beanInfo = getBeanInfo();
        return Arrays.stream(beanInfo.getPropertyDescriptors())
            .filter(p -> !p.getName().equals("class"))
            .collect(Collectors.toMap(ResultSetMapper::columnLabel, ResultSetMapper::columnMapper));
    }

    private static String columnLabel(PropertyDescriptor propertyDescriptor) {
        String propertyName = propertyDescriptor.getName();
        StringBuilder columnLabel = new StringBuilder();
        int i = 0;
        while (i < propertyName.length()) {
            char current = propertyName.charAt(i);
            if (i == 0) {
                // first char
                columnLabel.append(Character.toLowerCase(current));
            } else if (i == propertyName.length() - 1) {
                // last char
                columnLabel.append(Character.toLowerCase(current));
            } else {
                // middle char
                char last = propertyName.charAt(i - 1);
                char next = propertyName.charAt(i + 1);
                if ((Character.isLowerCase(last) && Character.isUpperCase(current))
                    || (Character.isUpperCase(current) && Character.isLowerCase(next))) {
                    columnLabel.append('_').append(Character.toLowerCase(current));
                } else {
                    columnLabel.append(Character.toLowerCase(current));
                }
            }
            i++;
        }
        return columnLabel.toString();
    }

    private static ColumnMapper columnMapper(PropertyDescriptor propertyDescriptor) {
        Class<?> propertyType = propertyDescriptor.getPropertyType();
        return MAPPINGS.get(propertyType);
    }

}
