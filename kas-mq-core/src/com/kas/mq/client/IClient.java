package com.kas.mq.client;

import com.kas.infra.base.IObject;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqQueue;

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
  public abstract void connect(String host, int port);
  
  /**
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   * @return the {@link MqQueue} object if queue was opened, {@code null} otherwise
   */
  public abstract MqQueue open(String queue);
  
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
}
