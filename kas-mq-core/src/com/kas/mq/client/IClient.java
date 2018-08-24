package com.kas.mq.client;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;

/**
 * This is the interface all KAS/MQ clients should implement. 
 * 
 * @author Pippo
 */
public interface IClient
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
   * Get the connection status.<br>
   * <br>
   * A connected socket is one that is marked as connected AND not closed.
   * 
   * @return {@code true} if socket is connected and not closed, {@code false} otherwise
   * 
   * @see java.net.Socket#isConnected()
   */
  public abstract boolean isConnected();
  
  /**
   * Switch login credentials
   * 
   * @param user The user's name
   * @param pwd The user's password
   * @return {@code true} if {@code password} matches the user's password as defined in {@link MqConfiguration},
   * {@code false} otherwise
   */
  public abstract boolean login(String user, String pwd);
  
  /**
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   */
  public abstract void open(String queue);
  
  /**
   * Close the specified queue.
   */
  public abstract void close();
  
  /**
   * Get the queue status.
   * 
   * @return {@code true} if queue is open, {@code false} otherwise
   */
  public abstract boolean isOpen();
  
  /**
   * Define the specified queue.
   * 
   * @param queue The queue name to define.
   * @return {@code true} if queue was defined, {@code false} otherwise
   */
  public abstract boolean define(String queue);
  
  /**
   * Delete the specified queue.
   * 
   * @param queue The queue name to delete.
   * @return {@code true} if queue was deleted, {@code false} otherwise
   */
  public abstract boolean delete(String queue);
  
  /**
   * Show information regarding current session
   */
  public abstract void show();
  
  /**
   * Get a {@link MqMessage message} from the opened queue
   * 
   * @param priority The priority of the message to retrieve
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public abstract IMqMessage<?> get(int priority, long timeout, long interval);
  
  /**
   * Put a {@link MqMessage} into the opened queue.
   * 
   * @param message The {@link MqMessage} to put into the opened queue
   */
  public abstract void put(IMqMessage<?> message);
  
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
