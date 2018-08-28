package com.kas.mq;

import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IStoppable;

/**
 * A KAS/MQ application is one that has initialization/termination phases which occur
 * prior and after the running phase.<br>
 * <br>
 * Also, the application provides means of "stop" state.
 * 
 * @author Pippo
 */
public interface IKasMqAppl extends IInitializable, IStoppable
{
  /**
   * Initializing the application. 
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise
   * 
   * @see IInitializable#init()
   */
  public abstract boolean init();

  /**
   * Terminating the application.
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise
   * 
   * @see IInitializable#term();
   */
  public abstract boolean term();
  
  /**
   * Running the application.
   */
  public abstract void run();
  
  /**
   * Indicate that the caller wants the object to change its state to "stopping" 
   * 
   * @see IStoppable#stop()
   */
  public abstract void stop();
  
  /**
   * Get indication if the object is in "stopping" state
   * 
   * @return indication if the object is in "stopping" state
   * 
   * @see IStoppable#isStopping()
   */
  public abstract boolean isStopping();
}
