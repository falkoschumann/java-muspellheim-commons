/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import lombok.*;

/**
 * Collect information for a database connection.
 */
@Value
@RequiredArgsConstructor(staticName = "of")
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DatabaseConfiguration {

    /**
     * Host ip or hostname.
     *
     * @return the host.
     */
    String host;

    /**
     * Port.
     *
     * @return the port.
     */
    int port;

    /**
     * Username.
     *
     * @return the user.
     */
    @NonNull String user;

    /**
     * User password.
     *
     * @return the passwors.
     */
    String password;

    /**
     * Database name.
     *
     * @return the database.
     */
    @NonNull String database;

}
