/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

/**
 * Thrown if configuration was not found, for example if file does not exist.
 */
public class ConfigurationNotFoundException extends ConfigurationException {

    public ConfigurationNotFoundException(String message) {
        super(message);
    }

    public ConfigurationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
