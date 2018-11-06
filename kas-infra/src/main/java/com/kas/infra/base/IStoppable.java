package com.kas.infra.base;

/**
 * Implementors of this interface are state-based objects that can be "stopped" if necessary 
 * 
 * @author Pippo
 */
public interface IStoppable
{
  /**
   * Indicate that the caller wants the object to change its state to "stopping" 
   */
  public abstract void stop();
  
  /**
   * Get indication if the object is in "stopping" state
   * 
   * @return indication if the object is in "stopping" state
   */
  public abstract boolean isStopping();
}
