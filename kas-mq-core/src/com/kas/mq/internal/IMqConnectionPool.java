package com.kas.mq.internal;

import com.kas.infra.base.IObject;

/**
 * A connection pool
 * 
 * @author Pippo
 */
public interface IMqConnectionPool extends IObject
{
  /**
   * Allocate a new {@link MqConnection}
   * 
   * @return the newly allocated connection
   */
  public abstract MqConnection allocate();
  
  /**
   * Release the specified connection
   * 
   * @param conn {@link MqServerConnection} to be released
   */
  public abstract void release(MqConnection conn);
}
