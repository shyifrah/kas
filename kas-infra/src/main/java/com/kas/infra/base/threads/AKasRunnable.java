package com.kas.infra.base.threads;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;

/**
 * A {@link Runnable} object that equipped with a "stop" flag
 * 
 * @author Pippo
 */
public abstract class AKasRunnable extends AKasObject implements Runnable
{
  /**
   * Stop flag
   */
  protected boolean mStop = false;
  
  /**
   * Running the task.
   * 
   * @see java.lang.Runnable#run()
   */
  public abstract void run();

  /**
   * Mark the house keeper task it should stop
   */
  public synchronized void stop()
  {
    mStop = true;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public abstract String toPrintableString(int level);
}
