package com.kas.mq.impl.internal;

import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
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
   * 
   * @throws KasException if client failed to connect to KAS/MQ server
   */
  public abstract void connect(String host, int port) throws KasException;
  
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
   * Authenticate client against the KAS/MQ server.
   * 
   * @param user The user's name
   * @param pwd The user's password
   * 
   * @return {@code true} if user successfully authenticated, {@code false} otherwise
   */
  public abstract boolean login(String user, String pwd);
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public abstract boolean defineQueue(String queue, int threshold);
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   */
  public abstract boolean deleteQueue(String queue, boolean force);
  
  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all if {@code true}, display all information on all queues 
   * @return the queues that matched the query
   */
  public abstract Properties queryQueue(String name, boolean prefix, boolean all);
  
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
