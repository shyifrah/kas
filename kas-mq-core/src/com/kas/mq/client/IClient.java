package com.kas.mq.client;

import com.kas.comm.impl.NetworkAddress;
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
   * @param host The host name or IP address
   * @param port The port number
   * @param user The user's name
   * @param pwd The user's password
   */
  public abstract void connect(String host, int port, String user, String pwd);
  
  /**
   * Disconnect from the queue manager.
   */
  public abstract void disconnect();
  
  /**
   * Get the connection status
   * 
   * @return {@code true} if the client is connected to a remote host, {@code false} otherwise
   */
  public abstract boolean isConnected();
  
  /**
   * Get the {@link NetworkAddress} for this {@link IClient} object
   * 
   * @return the {@link NetworkAddress} for this {@link IClient} object
   */
  public abstract NetworkAddress getNetworkAddress();
  
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
   * @param timeout The number of milliseconds
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
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   */
  public abstract String getResponse();
  
  /**
   * Set {@link IClient} response to {@code response}.
   * 
   * @param response The text that will be saved for {@link #getResponse} call
   */
  public abstract void setResponse(String response);
}
