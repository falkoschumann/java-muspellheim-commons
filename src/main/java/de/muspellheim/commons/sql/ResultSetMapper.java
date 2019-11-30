/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Mapping a result set to an entity.
 *
 * <p>Use static methods to map individual columns or one of the sub classes {@link
 * ConstructorResultSetMapper} or {@link SetterResultSetMapper} to map a whole entity.
 *
 * <p>Column labels are mapped to property names by following convention: a column label with snake
 * case is mapped to a property name with camel case. For example the column label <code>
 * day_of_birth</code> is mapped to property name <code>dayOfBirth</code>.
 *
 * <p>Default registered column mappers exists for:
 *
 * <ul>
 *   <li><code>String</code>
 *   <li><code>Long</code>/<code>long</code>
 *   <li><code>Integer</code>/<code>int</code>
 *   <li><code>Short</code>/<code>short</code>
 *   <li><code>Byte</code>/<code>byte</code>
 *   <li><code>Double</code>/<code>double</code>
 *   <li><code>Float</code>/<code>float</code>
 *   <li><code>Boolean</code>/<code>boolean</code>
 *   <li><code>byte[]</code>
 *   <li><code>LocalDate</code>
 *   <li><code>LocalTime</code>
 *   <li><code>LocalDateTime</code>
 *   <li><code>Instant</code>
 * </ul>
 *
 * @param <T> the entity type.
 * @see ConstructorResultSetMapper
 * @see SetterResultSetMapper
 */
public abstract class ResultSetMapper<T> {

  private static final Map<Class<?>, ColumnMapper<?>> mappings = new HashMap<>();

  static {
    registerMapping(String.class, ResultSet::getString);

    registerMapping(Long.class, ResultSetMapper::getLong);
    registerMapping(long.class, ResultSetMapper::getLong);
    registerMapping(Integer.class, ResultSetMapper::getInteger);
    registerMapping(int.class, ResultSetMapper::getInteger);
    registerMapping(Short.class, ResultSetMapper::getShort);
    registerMapping(short.class, ResultSetMapper::getShort);
    registerMapping(Byte.class, ResultSetMapper::getByte);
    registerMapping(byte.class, ResultSetMapper::getByte);

    registerMapping(Double.class, ResultSetMapper::getDouble);
    registerMapping(double.class, ResultSetMapper::getDouble);
    registerMapping(Float.class, ResultSetMapper::getFloat);
    registerMapping(float.class, ResultSetMapper::getFloat);

    registerMapping(Boolean.class, ResultSetMapper::getBoolean);
    registerMapping(boolean.class, ResultSetMapper::getBoolean);

    registerMapping(byte[].class, ResultSet::getBytes);

    registerMapping(
        LocalDate.class,
        (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalDate.class));
    registerMapping(
        LocalTime.class,
        (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalTime.class));
    registerMapping(
        LocalDateTime.class,
        (resultSet, columnLabel) -> resultSet.getObject(columnLabel, LocalDateTime.class));
    registerMapping(
        Instant.class, (resultSet, columnLabel) -> resultSet.getObject(columnLabel, Instant.class));
  }

  private final Class<T> type;
  private final Map<String, String> mappedColumns;
  private final Map<String, ColumnMapper<?>> typeMappings;

  /**
   * Create a mapper.
   *
   * @param type the mapped entity type
   */
  public ResultSetMapper(Class<T> type) {
    this.type = type;
    mappedColumns = createMappedColumns();
    typeMappings = createMappings();
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
   * @param type the mapped property type
   * @param columnMapper the column mapper
   * @param <T> the mapped property type
   */
  public static <T> void registerMapping(Class<T> type, ColumnMapper<T> columnMapper) {
    mappings.put(type, columnMapper);
  }

  /**
   * Map a result set to a a value.
   *
   * @param resultSet a result set
   * @param columnLabel a column label
   * @param type the mapped value class
   * @param <T> the mapped value type
   * @return the mapped value
   * @throws SQLException if an error occurred
   */
  public static <T> T mapValue(ResultSet resultSet, String columnLabel, Class<T> type)
      throws SQLException {
    return getColumnMapper(type).map(resultSet, columnLabel);
  }

  /**
   * Map a result set to a list of values.
   *
   * @param resultSet a result set
   * @param type the mapped value class
   * @param <T> the mapped value type
   * @return the mapped value list
   * @throws SQLException if an error occurred
   */
  public static <T> List<T> mapValueList(ResultSet resultSet, Class<T> type) throws SQLException {
    ColumnMapper<T> columnMapper = getColumnMapper(type);
    String columnLabel = resultSet.getMetaData().getColumnLabel(1);
    List<T> values = new ArrayList<>();
    while (resultSet.next()) {
      T e = columnMapper.map(resultSet, columnLabel);
      values.add(e);
    }
    return values;
  }

  /**
   * Return the double value of a column or <code>null</code>.
   *
   * @param resultSet a result set
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
   * @param resultSet a result set
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
   * @param resultSet a result set
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
   * @param resultSet a result set
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
   * Return the short value of a column or <code>null</code>.
   *
   * @param resultSet a result set
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
   * @param resultSet a result set
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
   * @param resultSet a result set
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
   * Obtains the mapper for a given type.
   *
   * @param type the class to map
   * @param <T> the type to map
   * @return the column mapper
   */
  @SuppressWarnings("unchecked")
  protected static <T> ColumnMapper<T> getColumnMapper(Class<T> type) {
    ColumnMapper<T> columnMapper = (ColumnMapper<T>) mappings.get(type);
    if (columnMapper == null) {
      throw new NoSuchElementException("no column mapper found for: " + type);
    }
    return columnMapper;
  }

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
   * @param resultSet a result set
   * @param columnLabel a column label
   * @return the value of given column
   * @throws SQLException if an error occurred
   * @see #registerMapping(Class, ColumnMapper)
   */
  protected Object mapColumn(ResultSet resultSet, String columnLabel) throws SQLException {
    ColumnMapper<?> mapper = typeMappings.get(columnLabel);
    return mapper.map(resultSet, columnLabel);
  }

  private Map<String, String> createMappedColumns() {
    BeanInfo beanInfo = getBeanInfo();
    return Arrays.stream(beanInfo.getPropertyDescriptors())
        .filter(p -> !p.getName().equals("class"))
        .collect(Collectors.toMap(ResultSetMapper::columnLabel, FeatureDescriptor::getName));
  }

  private Map<String, ColumnMapper<?>> createMappings() {
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

  private static ColumnMapper<?> columnMapper(PropertyDescriptor propertyDescriptor) {
    Class<?> propertyType = propertyDescriptor.getPropertyType();
    return getColumnMapper(propertyType);
  }
}
