/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * Factory for an Postgres datasource.
 *
 * <p>The datasource will be lazy initialized and is a singleton.
 */
public class PostgresDatabase {

  private final DatabaseConfiguration configuration;

  private DataSource dataSource;

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
      dataSource = createDataSource();
    }
    return dataSource;
  }

  private DataSource createDataSource() {
    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setServerName(configuration.getHost());
    ds.setPortNumber(configuration.getPort());
    ds.setUser(configuration.getUser());
    ds.setPassword(configuration.getPassword());
    ds.setDatabaseName(configuration.getDatabase());
    return ds;
  }
}
