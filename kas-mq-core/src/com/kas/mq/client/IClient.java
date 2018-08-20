package com.kas.mq.client;

import com.kas.infra.base.IObject;
import com.kas.mq.impl.MqMessage;

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
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   * @return {@code true} if queue was opened, {@code false} otherwise
   */
  public abstract boolean open(String queue);
  
  /**
   * Close the specified queue.
   */
  public abstract void close();
  
  /**
   * Show information regarding current session
   */
  public abstract void show();
  
  /**
   * Get a {@link MqMessage message} from the opened queue
   * 
   * @param timeout The number of milliseconds to wait until a message available
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public abstract MqMessage get(int timeout);
  
  /**
   * Put a {@link MqMessage} into the opened queue.
   * 
   * @param message The {@link MqMessage} to put into the opened queue
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
