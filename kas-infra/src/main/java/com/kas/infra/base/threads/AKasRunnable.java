package com.kas.infra.base.threads;

import com.kas.infra.base.AKasObject;

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
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
