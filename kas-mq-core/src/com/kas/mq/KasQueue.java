package com.kas.mq;

import java.io.File;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.RunTimeUtils;

/**
 * A {@link KasQueue} object is the simplest destination that is managed by the KAS/MQ system.
 */
public class KasQueue extends AKasObject
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
   * The actual message container. An array of {@link KasMessageDeque} objects, one for each priority.<br>
   * <br>
   * When a message with priority of 0 is received by this {@link KasQueue} object, it is stored in the 
   * {@link KasMessageDeque} at index 0 of the array. A message with priority of 1 is stored at index 1 etc.
   */
  protected transient KasMessageDeque [] mQueueArray;
  
  /**
   * The file backing up this {@link KasQueue} object.
   */
  protected transient File    mBackupFile = null;
  protected transient String  mBackupFileName = null;
  
  /**
   * Constructing a {@link KasQueue} object with the specified name.
   * 
   * @param name The name of this {@link KasQueue} object.
   */
  public KasQueue(String name)
  {
    mName       = name;
    mQueueId    = UniqueId.generate();
    mQueueArray = new KasMessageDeque[ KasMessage.cMaximumPriority + 1 ];
    for (int i = 0; i <= KasMessage.cMaximumPriority; ++i)
      mQueueArray[i] = new KasMessageDeque();
  }
  
  /**
   * Put a message into this {@link KasQueue} object.
   * 
   * @param message The message that should be stored at this {@link KasQueue} object.
   * @return {@code true} if message was added, {@code false} otherwise.
   */
  public boolean put(KasMessage message)
  {
    if (message == null)
      return false;
    
    int prio = message.getPriority();
    return mQueueArray[prio].offer(message);
  }
  
  /**
   * Gets a message with a specific priority from this {@link KasQueue} object
   * 
   * @param priority The priority of the message to be retrieved. Default value is 0.
   * @return the returned message or {@code null} if there is no message with the specified priority.
   */
  public KasMessage get()
  {
    return get(KasMessage.cMinimumPriority);
  }
  
  public KasMessage get(int priority)
  {
    if ((priority < KasMessage.cMinimumPriority) || (priority > KasMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    return mQueueArray[priority].poll();
  }
  
  /**
   * Gets a message with a specific priority from this {@link KasQueue} object, and wait indefinitely if one is not available.
   * 
   * @param priority The priority of the message to be retrieved. Default value is 0
   * @return the returned message
   */
  public KasMessage getAndWait()
  {
    return getAndWait(KasMessage.cMinimumPriority);
  }
  
  public KasMessage getAndWait(int priority)
  {
    if ((priority < KasMessage.cMinimumPriority) || (priority > KasMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    KasMessage result = null;
    try
    {
      result = mQueueArray[priority].take();
    }
    catch (InterruptedException e) {}
    return result;
  }
  
  /**
   * Gets a message with a specific priority from this {@link KasQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code interval} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param priority The priority of the message to be retrieved. Default value is 0
   * @param interval The number of milliseconds to delay between each polling operation. Default value is 1 second
   * @param timeout The number of milliseconds to wait until the get operation is aborted.
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public KasMessage getAndWaitWithTimeout(long timeout)
  {
    return getAndWaitWithTimeout(KasMessage.cMinimumPriority, timeout, 1000L);
  }
  
  public KasMessage getAndWaitWithTimeout(int priority, long timeout)
  {
    return getAndWaitWithTimeout(priority, timeout, 1000L);
  }
  
  public KasMessage getAndWaitWithTimeout(int priority, long timeout, long interval)
  {
    if ((priority < KasMessage.cMinimumPriority) || (priority > KasMessage.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    if (timeout <= 0)
      return null;
    
    KasMessage result = get(priority);
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
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  UniqueId=").append(mQueueId.toString()).append("\n")
      .append(pad).append("  Queues=(\n");
    
    for (int i = 0; i < mQueueArray.length; ++i)
      sb.append(pad).append("    P").append(String.format("%d", i)).append("={size=[").append(mQueueArray[i].size()).append("]}\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
