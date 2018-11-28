package com.kas.appl;

import com.kas.infra.base.IInitializable;
import com.kas.infra.config.IBaseListener;

/**
 * A KAS application is one that has initialization/termination phases which occur
 * prior and after the running phase.<br>
 * <br>
 * Also, the application provides means of "stop" state.
 * 
 * @author Pippo
 */
public interface IKasApp extends IInitializable, IBaseListener
{
  /**
   * Get the application name
   * 
   * @return the application name
   */
  public abstract String getAppName();
  
  /**
   * Initializing the application.  
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise
   */
  public abstract boolean appInit();

  /**
   * Terminating the application.
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise
   */
  public abstract boolean appTerm();
  
  /**
   * Running the application. 
   */
  public abstract void appExec();
}
