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
  
  /**
   * Returns the {@link IRunnable} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IRunnable}.
   * 
   * @return a replica of this {@link IRunnable}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IRunnable replicate();
  
  /**
   * Returns the {@link IRunnable} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
