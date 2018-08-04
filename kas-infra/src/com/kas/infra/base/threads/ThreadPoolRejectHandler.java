package com.kas.infra.base.threads;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import com.kas.infra.base.AKasObject;

public class ThreadPoolRejectHandler extends AKasObject implements RejectedExecutionHandler
{
  /**
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
  
  /**
   * Returns a replica of this {@link #ThreadPoolRejectHandler}.
   * 
   * @return a replica of this {@link #ThreadPoolRejectHandler}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ThreadPoolRejectHandler replicate()
  {
    return new ThreadPoolRejectHandler();
  }

  /**
   * Get the object's detailed string representation. For {@code ThreadPoolRejectHandler}, this method returns the same 
   * result as {@link #name()}.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
