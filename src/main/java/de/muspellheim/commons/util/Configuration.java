/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import lombok.NonNull;

/**
 * Uses properties files and system properties as configuration.
 *
 * <p>A system property override a property from file. A default configuration can be load from
 * class path.
 */
public class Configuration {

  private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

  private final Properties properties;

  /** Create an empty configuration. */
  public Configuration() {
    this(new Properties());
  }

  /**
   * Initialize a configuration.
   *
   * @param properties initial configuration
   */
  public Configuration(@NonNull Properties properties) {
    this.properties = properties;
  }

  /**
   * Load default configuration from class path.
   *
   * <p>Load a properties file from class path <code>/{basename}.properties</code>.
   *
   * @param basename basename of properties file
   * @throws ConfigurationException if default configuration can not be loaded
   */
  public void loadDefaults(@NonNull String basename) throws ConfigurationException {
    String filename = String.format("/%s.properties", basename);
    InputStream in = getClass().getResourceAsStream(filename);
    if (in == null) {
      throw new ConfigurationNotFoundException("Defaults not exists");
    }

    try (Reader reader = new InputStreamReader(in, CHARSET)) {
      properties.load(reader);
    } catch (IOException e) {
      throw new ConfigurationException("Defaults could not load", e);
    }
  }

  /**
   * Load configuration from working directory.
   *
   * <p>Load a properties file from working directory <code>{basename}.properties</code>.
   *
   * @param basename basename of properties file
   * @throws ConfigurationException if configuration can not be loaded
   */
  public void load(@NonNull String basename) throws ConfigurationException {
    String filename = String.format("%s.properties", basename);
    Path propertiesFile = Paths.get(filename);
    load(propertiesFile);
  }

  /**
   * Load configuration from any path.
   *
   * @param propertiesFile path to a properties file
   * @throws ConfigurationException if configuration can not be loaded
   */
  public void load(@NonNull Path propertiesFile) throws ConfigurationException {
    if (!Files.exists(propertiesFile)) {
      throw new ConfigurationNotFoundException("File not found: " + propertiesFile);
    }

    try (Reader reader = Files.newBufferedReader(propertiesFile, CHARSET)) {
      properties.load(reader);
    } catch (IOException e) {
      throw new ConfigurationException("Configuration could not load", e);
    }
  }

  /**
   * Get an existing value from configuration.
   *
   * @param key key for value
   * @return the value
   * @throws ConfigurationException if value could not get
   * @throws NotConfiguredException if key not exists in configuration
   */
  public String getString(String key) throws ConfigurationException {
    return getValue(key).orElseThrow(() -> new NotConfiguredException(key));
  }

  /**
   * Get a value from configuration or default value if key not exists.
   *
   * @param key key for value
   * @param defaultValue value if key not exists
   * @return the value
   * @throws ConfigurationException if value could not get
   */
  public String getString(String key, String defaultValue) throws ConfigurationException {
    return getValue(key).orElse(defaultValue);
  }

  private Optional<String> getValue(String key) {
    Optional<String> value = Optional.ofNullable(System.getProperty(key));
    if (value.isPresent()) {
      return value;
    }
    return Optional.ofNullable(properties.getProperty(key));
  }

  /**
   * Get an existing value from configuration.
   *
   * @param key key for value
   * @return the value
   * @throws ConfigurationException if value could not get
   * @throws NotConfiguredException if key not exists in configuration
   */
  public boolean getBoolean(String key) throws ConfigurationException {
    return getValue(key)
        .map(Boolean::parseBoolean)
        .orElseThrow(() -> new NotConfiguredException(key));
  }

  /**
   * Get a value from configuration or default value if key not exists.
   *
   * @param key key for value
   * @param defaultValue value if key not exists
   * @return the value
   * @throws ConfigurationException if value could not get
   */
  public boolean getBoolean(String key, boolean defaultValue) throws ConfigurationException {
    return getValue(key).map(Boolean::parseBoolean).orElse(defaultValue);
  }

  /**
   * Get an existing value from configuration.
   *
   * @param key key for value
   * @return the value
   * @throws ConfigurationException if value could not get
   * @throws NotConfiguredException if key not exists in configuration
   */
  public int getInt(String key) throws ConfigurationException {
    try {
      return getValue(key)
          .map(Integer::parseInt)
          .orElseThrow(() -> new NotConfiguredException(key));
    } catch (NumberFormatException e) {
      throw new ConfigurationException(key + " must be int", e);
    }
  }

  /**
   * Get a value from configuration or default value if key not exists.
   *
   * @param key key for value
   * @param defaultValue value if key not exists
   * @return the value
   * @throws ConfigurationException if value could not get
   */
  public int getInt(String key, int defaultValue) throws ConfigurationException {
    try {
      return getValue(key).map(Integer::parseInt).orElse(defaultValue);
    } catch (NumberFormatException e) {
      throw new ConfigurationException(key + " must be int", e);
    }
  }

  /**
   * Get an existing value from configuration.
   *
   * @param key key for value
   * @return the value
   * @throws ConfigurationException if value could not get
   * @throws NotConfiguredException if key not exists in configuration
   */
  public double getDouble(String key) throws ConfigurationException {
    try {
      return getValue(key)
          .map(Double::parseDouble)
          .orElseThrow(() -> new NotConfiguredException(key));
    } catch (NumberFormatException e) {
      throw new ConfigurationException(key + " must be double", e);
    }
  }

  /**
   * Get a value from configuration or default value if key not exists.
   *
   * @param key key for value
   * @param defaultValue value if key not exists
   * @return the value
   * @throws ConfigurationException if value could not get
   */
  public double getDouble(String key, double defaultValue) throws ConfigurationException {
    try {
      return getValue(key).map(Double::parseDouble).orElse(defaultValue);
    } catch (NumberFormatException e) {
      throw new ConfigurationException(key + " must be double", e);
    }
  }
}
