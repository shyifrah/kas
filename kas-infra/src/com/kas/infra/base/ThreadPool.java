package com.kas.infra.base;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool
{
  private static ScheduledThreadPoolExecutor sExecutor;
  static {
    sExecutor = new ScheduledThreadPoolExecutor(1);
    sExecutor.setMaximumPoolSize(20);
    sExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
    sExecutor.allowCoreThreadTimeOut(true);
  }
    
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  {
    return sExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
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
  public static boolean remove(Runnable command)
  {
    return sExecutor.remove(command);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static List<Runnable> shutdownNow()
  {
    return sExecutor.shutdownNow();
  }
}
