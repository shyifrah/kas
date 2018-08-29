package com.kas.mq;

import com.kas.infra.base.IInitializable;

/**
 * A KAS/MQ application is one that has initialization/termination phases which occur
 * prior and after the running phase.<br>
 * <br>
 * Also, the application provides means of "stop" state.
 * 
 * @author Pippo
 */
public interface IKasMqAppl extends IInitializable
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
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise 
   */
  public abstract boolean run();
}
