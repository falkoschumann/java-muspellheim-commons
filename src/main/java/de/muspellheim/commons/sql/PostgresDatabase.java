/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import javax.sql.*;

import org.postgresql.ds.*;

/**
 * Factory for an Postgres datasource.
 * <p>
 * The datasource will be lazy initialized and is a singleton.
 */
public class PostgresDatabase {

    private final DatabaseConfiguration configuration;

    private PGSimpleDataSource dataSource;

    /**
     * Initialize with configuration.
     *
     * @param configuration the configuration
     */
    public PostgresDatabase(DatabaseConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Returns the data source for the underlying configuration.
     *
     * @return the data source
     */
    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new PGSimpleDataSource();
            dataSource.setServerName(configuration.getHost());
            dataSource.setPortNumber(configuration.getPort());
            dataSource.setUser(configuration.getUser());
            dataSource.setPassword(configuration.getPassword());
            dataSource.setDatabaseName(configuration.getDatabase());
        }
        return dataSource;
    }

}
