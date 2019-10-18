/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
