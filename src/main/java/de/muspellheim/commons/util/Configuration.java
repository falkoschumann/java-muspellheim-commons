/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Configuration {

    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    private final Properties properties;

    public Configuration() {
        this(new Properties());
    }

    public Configuration(Properties properties) {
        this.properties = Objects.requireNonNull(properties, "properties");
    }

    public void loadDefaults(String basename) throws ConfigurationException {
        String filename = String.format("/%s.properties", Objects.requireNonNull(basename, "basename"));
        InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new ConfigurationException("Defaults not exists");
        }

        try (Reader reader = new InputStreamReader(in, CHARSET)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new ConfigurationException("Defaults could not load", e);
        }
    }

    public void load(String basename) throws ConfigurationException {
        String filename = String.format("%s.properties", Objects.requireNonNull(basename, "basename"));
        Path propertiesFile = Paths.get(filename);
        load(propertiesFile);
    }

    public void load(Path propertiesFile) throws ConfigurationException {
        try (Reader reader = Files.newBufferedReader(Objects.requireNonNull(propertiesFile, "propertiesFile"), CHARSET)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new ConfigurationException("Configuration could not load", e);
        }
    }

    public String getString(String key) throws ConfigurationException {
        return getValue(key)
            .orElseThrow(() -> new NotConfiguredException(key));
    }

    public String getString(String key, String defaultValue) throws ConfigurationException {
        return getValue(key)
            .orElse(defaultValue);
    }

    private Optional<String> getValue(String key) {
        Optional<String> value = Optional.ofNullable(System.getProperty(key));
        if (value.isPresent()) {
            return value;
        }
        return Optional.ofNullable(properties.getProperty(key));
    }

    public boolean getBoolean(String key) throws ConfigurationException {
        return getValue(key)
            .map(Boolean::parseBoolean)
            .orElseThrow(() -> new NotConfiguredException(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) throws ConfigurationException {
        return getValue(key)
            .map(Boolean::parseBoolean)
            .orElse(defaultValue);
    }

    public int getInt(String key) throws ConfigurationException {
        try {
            return getValue(key)
                .map(Integer::parseInt)
                .orElseThrow(() -> new NotConfiguredException(key));
        } catch (NumberFormatException e) {
            throw new ConfigurationException(key + " must be int", e);
        }
    }

    public int getInt(String key, int defaultValue) throws ConfigurationException {
        try {
            return getValue(key)
                .map(Integer::parseInt)
                .orElse(defaultValue);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(key + " must be int", e);
        }
    }

    public double getDouble(String key) throws ConfigurationException {
        try {
            return getValue(key)
                .map(Double::parseDouble)
                .orElseThrow(() -> new NotConfiguredException(key));
        } catch (NumberFormatException e) {
            throw new ConfigurationException(key + " must be double", e);
        }
    }

    public double getDouble(String key, double defaultValue) throws ConfigurationException {
        try {
            return getValue(key)
                .map(Double::parseDouble)
                .orElse(defaultValue);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(key + " must be double", e);
        }
    }

}
