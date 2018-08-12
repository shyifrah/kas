package com.kas.mq.impl;

import java.io.File;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.mq.typedef.MessageDeque;

/**
 * A {@link MqQueue} object is the simplest destination that is managed by the KAS/MQ system.
 * 
 * @author Pippo
 */
public class MqQueue extends AKasObject
{
  /**
   * Name of this message queue
   */
  private String mName;
  
  /**
   * A UniqueId representing this message queue
   */
  private UniqueId mQueueId;
  
  /**
   * The actual message container. An array of {@link MessageDeque} objects, one for each priority.<br>
   * <br>
   * When a message with priority of 0 is received by this {@link MqQueue} object, it is stored in the 
   * {@link MessageDeque} at index 0 of the array. A message with priority of 1 is stored at index 1 etc.
   */
  protected transient MessageDeque [] mQueueArray;
  
  /**
   * The file backing up this {@link MqQueue} object.
   */
  protected transient File mBackupFile = null;
  
  /**
   * Constructing a {@link MqQueue} object with the specified name.
   * 
   * @param name The name of this {@link MqQueue} object.
   */
  public MqQueue(String name)
  {
    mName       = name;
    mQueueId    = UniqueId.generate();
    mQueueArray = new MessageDeque[ MqMessage.cMaximumPriority + 1 ];
    for (int i = 0; i <= MqMessage.cMaximumPriority; ++i)
      mQueueArray[i] = new MessageDeque();
  }
  
  /**
   * Put a message into this {@link MqQueue} object.
   * 
   * @param message The message that should be stored at this {@link MqQueue} object.
   * @return {@code true} if message was added, {@code false} otherwise.
   */
  public boolean put(MqMessage message)
  {
    if (message == null)
      return false;
    
    int prio = message.getPriority();
    return mQueueArray[prio].offer(message);
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object
   * 
   * @return the returned message or {@code null} if there is no message
   */
  public MqMessage get()
  {
    return get(MqMessage.cMaximumPriority);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object
   * 
   * @param priority The priority of the message to be retrieved
   * @return the returned message or {@code null} if there is no message
   */
  public MqMessage get(int priority)
  {
    if ((priority < MqMessage.cMinimumPriority) || (priority > MqMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    return mQueueArray[priority].poll();
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object, and wait indefinitely if one is not available.
   * 
   * @return the returned message
   */
  public MqMessage getAndWait()
  {
    return getAndWait(MqMessage.cMaximumPriority);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object, and wait indefinitely if one is not available.
   * 
   * @param priority The priority of the message to be retrieved 
   * @return the returned message
   */
  public MqMessage getAndWait(int priority)
  {
    if ((priority < MqMessage.cMinimumPriority) || (priority > MqMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    MqMessage result = null;
    try
    {
      result = mQueueArray[priority].take();
    }
    catch (InterruptedException e) {}
    return result;
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code 1000} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param timeout The number of milliseconds to wait until the get operation is aborted.
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(long timeout)
  {
    return getAndWaitWithTimeout(MqMessage.cMinimumPriority, timeout, 1000L);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code 1000} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param priority The priority of the message to be retrieved
   * @param timeout The number of milliseconds to wait until the get operation is aborted.
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(int priority, long timeout)
  {
    return getAndWaitWithTimeout(priority, timeout, 1000L);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code interval} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param priority The priority of the message to be retrieved
   * @param timeout The number of milliseconds to wait until the get operation is aborted
   * @param interval The number of milliseconds to delay between each polling operation
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(int priority, long timeout, long interval)
  {
    if ((priority < MqMessage.cMinimumPriority) || (priority > MqMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    if (timeout <= 0)
      return null;
    
    MqMessage result = get(priority);
    long millisPassed = 0;
    while ((result == null) && (millisPassed < timeout))
    {
      RunTimeUtils.sleepForMilliSeconds(interval);
      millisPassed += interval;
      result = get(priority);
    }
    
    return result;
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
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  UniqueId=").append(mQueueId.toString()).append("\n")
      .append(pad).append("  Queues=(\n");
    
    for (int i = 0; i < mQueueArray.length; ++i)
      sb.append(pad).append("    P").append(String.format("%02d=(", i)).append(mQueueArray[i].toPrintableString(0)).append(")\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}