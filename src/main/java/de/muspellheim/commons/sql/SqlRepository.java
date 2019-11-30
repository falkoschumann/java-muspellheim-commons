/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import de.muspellheim.commons.domain.AuthorizationException;
import de.muspellheim.commons.domain.ConnectionException;
import de.muspellheim.commons.domain.RepositoryException;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Base class for SQL based repository.
 *
 * <p>Can used as instance also.
 */
public class SqlRepository {

  protected final ResourceBundle bundle = ResourceBundle.getBundle(SqlRepository.class.getName());

  /** SQL state class for connection exceptions. */
  public static final String CONNECTION_CLASS = "08";

  /** SQL state class for authorization exceptions. */
  public static final String AUTHORIZATION_CLASS = "28";

  /**
   * Map default SQL exception to repository exception.
   *
   * <p>Check for {@link ConnectionException} and {@link AuthorizationException}.
   *
   * @param e a SQL exception
   * @throws RepositoryException the repository exception
   */
  public void checkForRepositoryException(SQLException e) throws RepositoryException {
    if (e.getSQLState().startsWith(CONNECTION_CLASS)) {
      throw new ConnectionException(bundle.getString("sqlRepository.connectionException"), e);
    } else if (e.getSQLState().startsWith(AUTHORIZATION_CLASS)) {
      throw new AuthorizationException(bundle.getString("sqlRepository.authorizationException"), e);
    }
  }
}
