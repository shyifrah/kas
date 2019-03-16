package com.kas.infra.base.threads;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;

/**
 * A handler that will handle rejected work requests.<br>
 * Basically, it will simply force them into the work queue.
 * 
 * @see ThreadPoolWorkQueue#force(Runnable)
 * 
 * @author Pippo
 */
public class ThreadPoolRejectHandler extends AKasObject implements RejectedExecutionHandler
{
  /**
   * Forcing the {@code Runnable} into the work queue
   * 
   * @param r
   *   The task to be executed
   * @param executor
   *   The {@code ThreadPoolExecutor} from which the task was rejected 
   */
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
  {
    ThreadPoolWorkQueue wq = (ThreadPoolWorkQueue)executor.getQueue();
    wq.force(r);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
