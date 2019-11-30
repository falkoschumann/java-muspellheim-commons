/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import de.muspellheim.commons.domain.QueryCanceledException;
import de.muspellheim.commons.domain.RepositoryException;
import java.sql.SQLException;

/**
 * Base class for Postgres SQL repository.
 *
 * <p>Can used as instance also.
 */
public abstract class PostgresRepository extends SqlRepository {

  /** SQL state for query canceled. */
  public static final String QUERY_CANCELED = "57014";

  /**
   * Map default SQL exception to repository exception.
   *
   * <p>Check additional for {@link QueryCanceledException}.
   *
   * @param e a SQL exception
   * @throws RepositoryException the repository exception
   */
  @Override
  public void checkForRepositoryException(SQLException e) throws RepositoryException {
    super.checkForRepositoryException(e);
    if (QUERY_CANCELED.equals(e.getSQLState())) {
      throw new QueryCanceledException(bundle.getString("postgresRepository.queryCanceled"), e);
    }
  }
}
