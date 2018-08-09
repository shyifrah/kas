package com.kas.mq.server.internal;

import com.kas.infra.base.IObject;

/**
 * {@link IController} is an interface that will provide functionality for {@link ClientHandler ClientHandlers}.
 * @author Pippo
 */
public interface IController extends IObject
{
  /**
   * Get the controller's MQ configuration
   * 
   * @return the controller's MQ configuration
   */
  public abstract MqConfiguration getConfig();
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link IObject}.
   * 
   * @return a replica of this {@link IObject}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
