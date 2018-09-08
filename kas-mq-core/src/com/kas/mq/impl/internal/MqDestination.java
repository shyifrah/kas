package com.kas.mq.impl.internal;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;

/**
 * A {@link MqDestination} object is the simplest destination that is managed by the KAS/MQ system.
 * 
 * @author Pippo
 */
public abstract class MqDestination extends AKasObject
{
  /**
   * Logger
   */
  protected transient ILogger mLogger;
  
  /**
   * Name of the manager that owns this destination
   */
  protected MqManager mManager;
  
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
  MqDestination(MqManager mgr, String name)
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
  public MqManager getManager()
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
   * Put a message into this {@link MqDestination} object.
   * 
   * @param message The message that should be stored
   * @return {@code true} if message was stored, {@code false} otherwise.
   */
  public abstract boolean put(IMqMessage<?> message);
  
  /**
   * Get a message and wait indefinitely for one to be available.
   * 
   * @return The {@link AMqMessage}
   */
  public abstract IMqMessage<?> get();
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.
   * 
   * @return The {@link AMqMessage} or {@code null} if one is unavailable
   */
  public abstract IMqMessage<?> get(long timeout);
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.<br>
   * <br>
   * Execution is suspended for {@code interval} milliseconds between each polling operation.
   * 
   * @return The {@link AMqMessage} or {@code null} if one is unavailable
   */
  public abstract IMqMessage<?> get(long timeout, long interval);
  
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
      .append(pad).append("  Manager=").append(StringUtils.asPrintableString(mManager)).append("\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
