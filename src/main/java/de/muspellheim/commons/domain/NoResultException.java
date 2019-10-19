/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.domain;

/**
 * Thrown by a repository when an executed query expected single result and there is no result to return.
 */
public class NoResultException extends RepositoryException {

    public NoResultException(String message) {
        super(message);
    }

    public NoResultException(String message, Throwable cause) {
        super(message, cause);
    }

}
