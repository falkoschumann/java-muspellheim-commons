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

/**
 * Mapping a result set to an entity.
 * <p>
 * Use static methods to map individual columns or one of the sub classes {@link ConstructorResultSetMapper} or
 * {@link SetterResultSetMapper} to map a whole entity.
 * <p>
 * Column labels are mapped to property names by following convention: a column label with snake case is mapped to a
 * property name with camel case. For example the column label <code>day_of_birth</code> is mapped to property name
 * <code>dayOfBirth</code>.
 * <p>
 * Default registered column mappers exists for:
 * <ul>
 *     <li><code>String</code></li>
 *     <li><code>Long</code>/<code>long</code></li>
 *     <li><code>Integer</code>/<code>int</code></li>
 *     <li><code>Short</code>/<code>short</code></li>
 *     <li><code>Byte</code>/<code>byte</code></li>
 *     <li><code>Double</code>/<code>double</code></li>
 *     <li><code>Float</code>/<code>float</code></li>
 *     <li><code>Boolean</code>/<code>boolean</code></li>
 *     <li><code>byte[]</code></li>
 *     <li><code>LocalDate</code></li>
 *     <li><code>LocalTime</code></li>
 *     <li><code>LocalDateTime</code></li>
 *     <li><code>Instant</code></li>
 * </ul>
 *
 * @param <T> the entity type.
 * @see ConstructorResultSetMapper
 * @see SetterResultSetMapper
 */
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

    /**
     * Create a mapper.
     *
     * @param type the mapped entity type
     */
    public ResultSetMapper(Class<T> type) {
        this.type = type;
        mappedColumns = createMappedColumns();
        mappings = createMappings();
    }

    /**
     * The mapped entity type by this mapper.
     *
     * @return the mapped entity type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * The bean info for mapped entity type.
     *
     * @return the entity bean info
     */
    protected BeanInfo getBeanInfo() {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("Can not create name mapping", e);
        }
        return beanInfo;
    }

    /**
     * Register a new column mapper or replace a exists one.
     *
     * @param type         the mapped property type
     * @param columnMapper the column mapper
     * @param <T>          the mapped property type
     */
    public static <T> void registerMapping(Class<T> type, ColumnMapper<T> columnMapper) {
        MAPPINGS.put(type, columnMapper);
    }

    /**
     * Return the double value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Double getDouble(ResultSet resultSet, String columnLabel) throws SQLException {
        double v = resultSet.getDouble(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the float value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Float getFloat(ResultSet resultSet, String columnLabel) throws SQLException {
        float v = resultSet.getFloat(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the long value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Long getLong(ResultSet resultSet, String columnLabel) throws SQLException {
        long v = resultSet.getLong(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the integer value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Integer getInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        int v = resultSet.getInt(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the short value of a column or <code>null</code>
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Short getShort(ResultSet resultSet, String columnLabel) throws SQLException {
        short v = resultSet.getShort(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the byte value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Byte getByte(ResultSet resultSet, String columnLabel) throws SQLException {
        byte v = resultSet.getByte(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Return the boolean value of a column or <code>null</code>.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column or <code>null</code>
     * @throws SQLException if an error occurred
     */
    public static Boolean getBoolean(ResultSet resultSet, String columnLabel) throws SQLException {
        boolean v = resultSet.getBoolean(columnLabel);
        if (resultSet.wasNull()) {
            return null;
        }
        return v;
    }

    /**
     * Map a result set to a list of entities.
     *
     * @param resultSet a result set
     * @return the mapped entity list
     * @throws SQLException if an error occurred
     */
    public List<T> mapList(ResultSet resultSet) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T v = map(resultSet);
            list.add(v);
        }
        return list;
    }

    /**
     * Map a result set to an entity.
     *
     * @param resultSet a result set
     * @return the mapped entity
     * @throws SQLException if an error occurred
     */
    public abstract T map(ResultSet resultSet) throws SQLException;

    /**
     * Get the property name for a column label.
     *
     * @param columnLabel a column label
     * @return the associated property name
     */
    protected String propertyNameFor(String columnLabel) {
        return mappedColumns.get(columnLabel);
    }

    /**
     * Use known column mapper to get a column value.
     *
     * @param resultSet   a result set
     * @param columnLabel a column label
     * @return the value of given column
     * @throws SQLException if an error occurred
     * @see #registerMapping(Class, ColumnMapper)
     */
    protected Object mapColumn(ResultSet resultSet, String columnLabel) throws SQLException {
        ColumnMapper mapper = mappings.get(columnLabel);
        return mapper.map(resultSet, columnLabel);
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
        ColumnMapper columnMapper = MAPPINGS.get(propertyType);
        if (columnMapper == null) {
            throw new IllegalStateException("no column mapper found: " + propertyType);
        }
        return columnMapper;
    }

}
