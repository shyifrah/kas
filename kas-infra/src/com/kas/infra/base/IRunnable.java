package com.kas.infra.base;

/**
 * A Runnable {@link IObject}
 * 
 * @author Pippo
 */
public interface IRunnable extends IObject, Runnable
{
  /**
   * Running the {@link Runnable} object
   * 
   * @see java.lang.Runnable#run()
   */
  public abstract void run();
}
