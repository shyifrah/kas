package com.kas.infra.config;

import com.kas.infra.base.IObject;

/**
 * A configuration listener.
 *   
 * @author Pippo
 */
public interface IListener extends IObject
{
  /**
   * Refresh configuration.<br>
   * <br>
   * When called, the object should update its configuration
   */
  public abstract void refresh();
  
  /**
   * Returns the {@link #IListener} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link #IListener}.
   * 
   * @return a replica of this {@link #IListener}
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link #IListener} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   */
  public abstract String toPrintableString(int level);
}
