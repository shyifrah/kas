package com.kas.mq.internal;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;

/**
 * A {@link MqQueue} object is a message container that is managed by the KAS/MQ system.
 * 
 * @author Pippo
 */
public abstract class MqQueue extends AKasObject
{
  /**
   * Logger
   */
  protected transient ILogger mLogger;
  
  /**
   * The manager that owns this queue
   */
  protected MqManager mManager;
  
  /**
   * Name of the queue
   */
  protected String mName;
  
  /**
   * Constructing a {@link MqQueue} object with the specified name.
   * 
   * @param mgr The name of the manager that owns this {@link MqQueue}
   * @param name The name of this {@link MqQueue} object.
   */
  protected MqQueue(MqManager mgr, String name)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mManager = mgr;
    mName = name;
  }
  
  /**
   * Get the owner manager
   * 
   * @return the owner manager
   */
  public MqManager getManager()
  {
    return mManager;
  }
  
  /**
   * Get the name of the queue
   * 
   * @return the name of the queue
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Restore the {@link MqQueue} contents from the file system.<br>
   * <br>
   * The default implementation is empty and return {@code true}. Driven classes should decide
   * whether to implement it or not.
   * 
   * @return {@code true} if queue contents restored successfully, {@code false} otherwise
   */
  public boolean restore()
  {
    return true;
  }

  /**
   * Backup the {@link MqQueue} contents to file system.<br>
   * <br>
   * The default implementation is empty and return {@code true}. Driven classes should decide
   * whether to implement it or not.
   * 
   * @return {@code true} if completed writing all queue contents successfully, {@code false} otherwise
   */
  public boolean backup()
  {
    return true;
  }
  
  /**
   * Expire old messages.<br>
   * <br>
   * The default implementation is empty and return 0. Driven classes should decide
   * whether to implement it or not.
   * 
   * @return the total number of messages expired
   */
  public int expire()
  {
    return 0;
  }
  
  /**
   * Put a message into this {@link MqQueue} object.
   * 
   * @param message The message that should be put to this {@link MqQueue}
   * @return {@code true} if message was put, {@code false} otherwise
   */
  public boolean put(IMqMessage message)
  {
    mLogger.debug("MqQueue::put() - IN");
    
    boolean success = false;
    if (message != null)
    {
      success = internalPut(message);
    }
    
    mLogger.debug("MqQueue::put() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * The actual implementation of the Put method
   * 
   * @param message The message to put
   * @return {@code true} if message was put, {@code false} otherwise
   */
  protected abstract boolean internalPut(IMqMessage message);
  
  /**
   * Get a message and wait indefinitely for one to be available.
   * 
   * @return The {@link IMqMessage}
   */
  public IMqMessage get()
  {
    mLogger.debug("MqQueue::get() - IN");
    
    IMqMessage msg = internalGet(0, IMqConstants.cDefaultPollingInterval);
    
    mLogger.debug("MqQueue::get() - OUT");
    return msg;
  }
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.<br>
   * <br>
   * If {@code timeout} is 0, this method is equivalent to {@link #get()}.
   * 
   * @param timeout The amount of time to wait for message availability before giving up
   * @return The {@link IMqMessage} or {@code null} if one is unavailable
   * 
   * @throws IllegalArgumentException if {@code timeout} is lower than 0
   */
  public IMqMessage get(long timeout)
  {
    mLogger.debug("MqQueue::get() - IN, Timeout=" + timeout);
    
    if (timeout < 0)
      throw new IllegalArgumentException("Invalid timeout: " + timeout);
    
    IMqMessage msg = internalGet(timeout, IMqConstants.cDefaultPollingInterval);
    
    mLogger.debug("MqQueue::get() - OUT");
    return msg;
  }
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.<br>
   * <br>
   * Execution is suspended for {@code interval} milliseconds between each polling operation.
   * 
   * @param timeout The amount of time to wait for message availability before giving up
   * @param interval The polling interval length
   * @return The {@link IMqMessage} or {@code null} if one is unavailable
   * 
   * @throws IllegalArgumentException if {@code timeout} or {@code interval} are lower than 0
   */
  public IMqMessage get(long timeout, long interval)
  {
    mLogger.debug("MqQueue::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    if (timeout < 0)
      throw new IllegalArgumentException("Invalid timeout: " + timeout);
    
    if (interval <= 0)
      throw new IllegalArgumentException("Invalid polling interval: " + interval);
    
    IMqMessage msg = internalGet(timeout, interval);
    
    mLogger.debug("MqQueue::get() - OUT");
    return msg;
  }
  
  /**
   * The actual implementation of the Get method
   * 
   * @param message The message to put
   * @return {@code true} if message was put, {@code false} otherwise
   */
  protected abstract IMqMessage internalGet(long timeout, long interval);
  
  /**
   * Response to Query request
   * 
   * @param all Whether to include all data in the response
   * @return a string that describes the queue 
   */
  public String queryResponse(boolean all)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(mName);
    if (all)
      sb.append("\n  Owned by...: ").append(mManager.getName()).append('\n');
    return sb.toString();
  }
  
  /**
   * Get the object's string representation
   * 
   * @return the string representation
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("KMQ://").append(mManager.toString()).append('/').append(mName);
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
    return toString();
  }
}
