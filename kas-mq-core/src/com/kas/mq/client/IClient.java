package com.kas.mq.client;

import com.kas.infra.base.IObject;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.KasQueue;

/**
 * This is the interface all KAS/MQ clients should implement. 
 * 
 * @author Pippo
 */
public interface IClient extends IObject
{
  /**
   * Connect client to the queue manager defined on {@code destination}
   * 
   * @param destination A IP address or host name.
   */
  public abstract void connect(String destination);
  
  /**
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   * @return the {@link KasQueue} object if queue was opened, {@code null} otherwise
   */
  public abstract KasQueue open(String queue);
  
  /**
   * Close the specified queue.
   */
  public abstract void close();
  
  /**
   * Disconnect from the queue manager.
   */
  public abstract void disconnect();
  
  /**
   * Create a {@link MqMessage} object.
   * 
   * @return the {@link MqMessage} object.
   */
  public abstract MqMessage createMessage();
  
  /**
   * Get a message from the opened queue.
   * 
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public abstract MqMessage get();
  
  /**
   * Get a message from the opened queue and wait indefinitely if one is unavailable.
   * 
   * @return the {@link MqMessage} object
   */
  public abstract MqMessage getAndWait();
  
  /**
   * Get a message from the opened queue and wait for {@code timeout} milliseconds if one is unavailable.
   * 
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public abstract MqMessage getAndWaitWithTimeout(long timeout);
  
  /**
   * Put a {@link MqMessage} into the opened queue.
   * 
   * @param the {@link MqMessage} to put into the opened queue
   */
  public abstract void put(MqMessage message);
  
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
