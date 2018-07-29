package com.kas.infra.base.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static ThreadPoolRejectHandler     sHandler;
  private static ThreadPoolExecutor          sExecutor;
  private static ScheduledThreadPoolExecutor sSchedExecutor;
  
  static
  {
    sHandler       = new ThreadPoolRejectHandler();
    sSchedExecutor = new ScheduledThreadPoolExecutor(1, new KasThreadFactory("KasSchedThread"));
    sExecutor      = new ThreadPoolExecutor(1, 20, 60, TimeUnit.SECONDS, new ThreadPoolWorkQueue(0), new KasThreadFactory("KasThread"), sHandler);
    sExecutor.allowCoreThreadTimeOut(true);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  {
    return sSchedExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static ScheduledFuture<?> schedule(Callable<?> command, long delay, TimeUnit unit)
  {
    return sSchedExecutor.schedule(command, delay, unit);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static void execute(Runnable command)
  {
    sExecutor.execute(command);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static boolean removeSchedule(Runnable command)
  {
    return sSchedExecutor.remove(command);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static boolean removeTask(Runnable command)
  {
    return sExecutor.remove(command);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static void shutdownNow()
  {
    sExecutor.purge();
    sSchedExecutor.purge();
    
    sExecutor.shutdownNow();
    sSchedExecutor.shutdownNow();
  }
}
