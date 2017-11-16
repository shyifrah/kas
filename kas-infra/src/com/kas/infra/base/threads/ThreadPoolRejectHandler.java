package com.kas.infra.base.threads;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import com.kas.infra.base.AKasObject;

public class ThreadPoolRejectHandler extends AKasObject implements RejectedExecutionHandler
{
  /***************************************************************************************************************
   * Forcing the {@code Runnable} into the work queue
   * 
   * @param r the task to be executed
   * @param executor the {@code ThreadPoolExecutor} from which the task was rejected 
   */
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
  {
    ThreadPoolWorkQueue wq = (ThreadPoolWorkQueue)executor.getQueue();
    wq.force(r);
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
