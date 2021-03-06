/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.util;

/** Thrown if a key has no value configured. */
public class NotConfiguredException extends ConfigurationException {

  public NotConfiguredException(String message) {
    super(message);
  }

  public NotConfiguredException(String message, Throwable cause) {
    super(message, cause);
  }
}
