package com.kas.infra.base;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static class WorkQueue extends LinkedBlockingQueue<Runnable>
  {
    private static final long serialVersionUID = 1L;
    
    private int mMaxSize;
    WorkQueue(int maxSize)
    {
      mMaxSize = maxSize;
    }
    
    public boolean offer(Runnable cmd)
    {
      if (size() == mMaxSize)
        return false;
      
      return super.offer(cmd);
    }
    
    public boolean force(Runnable cmd)
    {
      return super.offer(cmd);
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static class WorkQueueHandler implements RejectedExecutionHandler
  {
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
    {
      WorkQueue q = (WorkQueue)executor.getQueue();
      q.force(r);
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static class NamingThreadFactory implements ThreadFactory
  {
    private int mSeq;
    private String mPrefix;
    
    NamingThreadFactory(String pref)
    {
      mPrefix = pref;
      mSeq = 0;
    }
    
    public Thread newThread(Runnable r)
    {
      String name;
      synchronized(this)
      {
        ++mSeq;
        name = mPrefix + "-" + mSeq;
      }
      return new Thread(r, name);
    }
  }
   
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static WorkQueueHandler            sHandler;
  private static ThreadPoolExecutor          sExecutor;
  private static ScheduledThreadPoolExecutor sSchedExecutor;
  
  static {
    sHandler       = new WorkQueueHandler();
    sSchedExecutor = new ScheduledThreadPoolExecutor(1, new NamingThreadFactory("KasSchedThread"));
    sExecutor      = new ThreadPoolExecutor(1, 20, 60, TimeUnit.SECONDS, new WorkQueue(1000), new NamingThreadFactory("KasThread"), sHandler);
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
    sExecutor.shutdownNow();
    sSchedExecutor.shutdownNow();
  }
}
