package com.kas.infra.base;

/**
 * A KAS managed stoppable object.<br>
 * <br>
 * This class implements some of the methods that described in {@link IStoppable} 
 * 
 * @author Pippo
 * 
 * @see com.kas.infra.base.IStoppable
 */
public abstract class AStoppable extends AKasObject implements IStoppable
{
  /**
   * The indicator whether the object should be stopped
   */
  private boolean mIsStopping = false;
  
  /**
   * Stopping the object.<br>
   * <br>
   * Note that in order to keep thread-safety, this method is synchronized.
   */
  public synchronized void stop()
  {
    mIsStopping = true;
  }
  
  /**
   * Get the state of the object.<br>
   * <br>
   * Note that a return value of {@code true} does not mean the object already stopped, but only
   * that it was marked for stopping.<br>
   * <br>
   * Also note that in order to keep thread-safety, this method is synchronized.
   * 
   * @return {@code true} if object is stopping, {@code false} otherwise.
   */
  public synchronized boolean isStopping()
  {
    return mIsStopping;
  }
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level the required level padding
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
