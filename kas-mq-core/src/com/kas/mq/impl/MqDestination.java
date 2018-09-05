package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * A {@link MqDestination} object is the simplest destination that is managed by the KAS/MQ system.
 * 
 * @author Pippo
 */
public class MqDestination extends AKasObject
{
  /**
   * Logger
   */
  protected transient ILogger mLogger;
  
  /**
   * Name of the manager that owns this destination
   */
  protected String mManager;
  
  /**
   * Name of the destination
   */
  protected String mName;
  
  /**
   * Constructing a {@link MqDestination} object with the specified name.
   * 
   * @param mgr The name of the manager that owns this {@link MqQueue}
   * @param name The name of this {@link MqQueue} object.
   */
  public MqDestination(String mgr, String name)
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mManager = mgr;
    mName    = name;
  }
  
  /**
   * Get the manager name
   * 
   * @return the manager name
   */
  public String getManager()
  {
    return mManager;
  }
  
  /**
   * Get the destination name
   * 
   * @return the destination name
   */
  public String getName()
  {
    return mName;
  }
  
  
  /**
   * Get the object's string representation
   * 
   * @return the string representation
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("KMQ://").append(mManager).append('/').append(mName);
    return sb.toString();
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Manager=").append(mManager).append("\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
