/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.domain;

/**
 * Thrown by a repository when a query is canceled without result.
 */
public class QueryCanceledException extends RepositoryException {

    public QueryCanceledException(String message) {
        super(message);
    }

    public QueryCanceledException(String message, Throwable cause) {
        super(message, cause);
    }

}
