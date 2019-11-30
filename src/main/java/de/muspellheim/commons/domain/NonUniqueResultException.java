/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.domain;

/**
 * Thrown by a repository when an executed query expected single result and there is more than one
 * result.
 */
public class NonUniqueResultException extends RepositoryException {

  public NonUniqueResultException(String message) {
    super(message);
  }

  public NonUniqueResultException(String message, Throwable cause) {
    super(message, cause);
  }
}
