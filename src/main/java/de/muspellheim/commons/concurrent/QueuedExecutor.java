/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.NonNull;

/** Executes tasks from a queue in a single thread. */
public class QueuedExecutor implements Executor {

  private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

  /**
   * Create a executor with a named thread.
   *
   * @param name the thread name
   */
  public QueuedExecutor(String name) {
    Runnable task = new BackgroundWorkerTask();
    Thread t = new Thread(task, name);
    t.setDaemon(true);
    t.start();
  }

  /**
   * Put a task in the queue.
   *
   * @param task a task
   */
  @Override
  public void execute(@NonNull Runnable task) {
    try {
      tasks.put(task);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  private class BackgroundWorkerTask implements Runnable {

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Runnable task = tasks.take();
          task.run();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
          Thread.currentThread()
              .getUncaughtExceptionHandler()
              .uncaughtException(Thread.currentThread(), e);
        }
      }
    }
  }
}
