package com.kas.infra.base.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A ThreadPool is an object starting and stopping threads according to the work load on the system.
 * 
 * @author Pippo
 */
public class ThreadPool
{
  static private ThreadPoolRejectHandler     sHandler;
  static private ThreadPoolExecutor          sExecutor;
  static private ScheduledThreadPoolExecutor sSchedExecutor;
  
  static
  {
    sHandler       = new ThreadPoolRejectHandler();
    sSchedExecutor = new ScheduledThreadPoolExecutor(1, new KasThreadFactory("KasSchedThread"));
    sExecutor      = new ThreadPoolExecutor(1, 20, 60, TimeUnit.SECONDS, new ThreadPoolWorkQueue(0), new KasThreadFactory("KasThread"), sHandler);
    sExecutor.allowCoreThreadTimeOut(true);
  }
  
  /**
   * Schedule a {@link Runnable} object for repeated execution
   * 
   * @param command
   *   A {@link Runnable} object to execute
   * @param initDelay
   *   The number of time-units to delay the {@link Runnable} execution
   * @param period
   *   The number of time-units to delay the {@link Runnable} subsequent executions
   * @param unit
   *   A {@link TimeUnit} value which represents the time unit for {@code initDelay} and {@code period}
   * @return
   *   a {@link ScheduledFuture} referencing to the task's return value
   * 
   * @see java.util.concurrent.ScheduledThreadPoolExecutor#schedule(Callable, long, TimeUnit)
   */
  static public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  {
    return sSchedExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
  }
  
  /**
   * Schedule a {@link Callable} object
   * 
   * @param command
   *   A {@link Callable} object to execute
   * @param delay
   *   The number of time-units to delay the {@link Callable} execution
   * @param unit
   *   A {@link TimeUnit} value which represents the time unit for {@code delay}
   * @return
   *   a {@link ScheduledFuture} referencing to the task's return value
   * 
   * @see java.util.concurrent.ScheduledThreadPoolExecutor#schedule(Callable, long, TimeUnit)
   */
  static public ScheduledFuture<?> schedule(Callable<?> command, long delay, TimeUnit unit)
  {
    return sSchedExecutor.schedule(command, delay, unit);
  }
  
  /**
   * Execute a {@link Runnable} object
   * 
   * @param command
   *   A {@link Runnable} object to execute
   * 
   * @see java.util.concurrent.ThreadPoolExecutor#execute(Runnable)
   */
  static public void execute(Runnable command)
  {
    sExecutor.execute(command);
  }
  
  /**
   * Remove a {@link Runnable} object from the {@link ScheduledThreadPoolExecutor} work queue
   * 
   * @param command
   *   A {@link Runnable} object to remove
   * @return
   *   the value returned from {@link java.util.concurrent.ThreadPoolExecutor#remove(Runnable)}
   * 
   * @see java.util.concurrent.ThreadPoolExecutor#remove(Runnable)
   */
  static public boolean removeSchedule(Runnable command)
  {
    return sSchedExecutor.remove(command);
  }
  
  /**
   * Remove a {@link Runnable} object from the {@link ThreadPoolExecutor} work queue
   * 
   * @param command
   *   A {@link Runnable} object to remove
   * @return
   *   the value returned from {@link java.util.concurrent.ThreadPoolExecutor#remove(Runnable)}
   * 
   * @see java.util.concurrent.ThreadPoolExecutor#remove(Runnable)
   */
  static public boolean removeTask(Runnable command)
  {
    return sExecutor.remove(command);
  }
  
  /**
   * Shutdown immediately the {@link ThreadPool} 
   */
  static public void shutdownNow()
  {
    sExecutor.purge();
    sSchedExecutor.purge();
    
    sExecutor.shutdownNow();
    sSchedExecutor.shutdownNow();
  }
}
