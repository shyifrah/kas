package com.kas.mq.client;

import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;
import com.kas.mq.impl.IMqMessage;

/**
 * This is the interface all KAS/MQ clients should implement. 
 * 
 * @author Pippo
 */
public interface IClient extends IObject
{
  /**
   * Connect client to the KAS/MQ server.
   * 
   * @param host The host name or IP address
   * @param port The port number
   * @param user The user's name
   * @param pwd The user's password
   * 
   * @throws KasException if client failed to connect to KAS/MQ server
   */
  public abstract void connect(String host, int port, String user, String pwd) throws KasException;
  
  /**
   * Disconnecting from the remote KAS/MQ server.
   * 
   * @throws KasException if client failed to disconnect from KAS/MQ server
   */
  public abstract void disconnect() throws KasException;
  
  /**
   * Get the connection status.
   * 
   * @return {@code true} if client is connected, {@code false} otherwise
   */
  public abstract boolean isConnected();
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public abstract boolean define(String queue);
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   */
  public abstract boolean delete(String queue);
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   */
  public abstract IMqMessage<?> get(String queue, long timeout, long interval);
  
  /**
   * Put a message into the opened queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   */
  public abstract void put(String queue, IMqMessage<?> message);
  
  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public abstract boolean shutdown();
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   */
  public abstract String getResponse();
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   */
  public abstract void setResponse(String response);
}
