package com.kas.infra.base;

/**
 * A {@link IStoppable} object is one that can be stopped by calling the {@link #stop()} method
 * and its state can be queried by calling {@link #isStopping()}.
 * 
 * @author Pippo
 */
public interface IStoppable extends IObject
{
  /**
   * Stopping the object
   */
  public abstract void stop();
  
  /**
   * Get the state of the object.<br>
   * <br>
   * Note that a return value of {@code true} does not mean the object already stopped, but only
   * that it was marked for stopping.
   * 
   * @return {@code true} if object is stopping, {@code false} otherwise.
   */
  public abstract boolean isStopping();
  
  /**
   * Returns the {@link IStoppable} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IStoppable}.
   * 
   * @return a replica of this {@link IStoppable}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IStoppable replicate();
  
  /**
   * Returns the {@link IStoppable} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
