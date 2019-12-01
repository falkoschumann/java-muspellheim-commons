/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.sql.DataSource;
import lombok.NonNull;

/**
 * A runnable future task for running a SQL query.
 *
 * @param <T> result type of the query
 */
public class SqlQueryTask<T> implements RunnableFuture<T> {

  // Possible state transitions:
  // NEW -> NORMAL
  // NEW -> EXCEPTIONAL
  // NEW -> CANCELLED
  private volatile int state;
  private static final int NEW = 0;
  private static final int NORMAL = 1;
  private static final int EXCEPTIONAL = 2;
  private static final int CANCELLED = 3;

  private CountDownLatch barrier = new CountDownLatch(1);

  private final DataSource dataSource;
  private final String sql;
  private final SqlQuery<T> query;

  private Statement statement;
  private volatile T result;
  private volatile Throwable exception;

  /**
   * Obtains a new task.
   *
   * @param dataSource the data source
   * @param sql the SQL query string to execute
   * @param query the handler, which executes statement and return result
   */
  public SqlQueryTask(
      @NonNull DataSource dataSource, @NonNull String sql, @NonNull SqlQuery<T> query) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.query = query;
  }

  @Override
  public boolean isCancelled() {
    return state == CANCELLED;
  }

  @Override
  public boolean isDone() {
    return state != NEW;
  }

  @Override
  public synchronized void run() {
    if (state != NEW) {
      return;
    }

    try (Connection connection = dataSource.getConnection()) {
      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        this.statement = statement;
        result = query.execute(statement);
        state = NORMAL;
      }
    } catch (Exception e) {
      if (state != CANCELLED) {
        exception = e;
        state = EXCEPTIONAL;
      }
    }
    barrier.countDown();
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    try {
      if (statement != null) {
        statement.cancel();
      }
      state = CANCELLED;
      return true;
    } catch (SQLException e) {
      return false;
    }
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
    if (state == NEW) {
      barrier.await();
    }
    return report();
  }

  @Override
  public T get(long timeout, @NonNull TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    if (state == NEW) {
      if (!barrier.await(timeout, unit)) {
        throw new TimeoutException();
      }
    }
    return report();
  }

  private T report() throws ExecutionException {
    switch (state) {
      case NORMAL:
        return result;
      case EXCEPTIONAL:
        throw new ExecutionException(exception);
      case CANCELLED:
        throw new CancellationException();
      default:
        throw new IllegalStateException("unknown state: " + state);
    }
  }
}
