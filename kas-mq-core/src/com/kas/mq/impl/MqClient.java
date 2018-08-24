package com.kas.mq.impl;

import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.client.MqClientImpl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;

/**
 * A KAS/MQ client
 * 
 * @author Pippo
 */
public final class MqClient
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The actual client
   */
  private MqClientImpl mDelegator;
  
  /**
   * Construct the client
   */
  public MqClient()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mDelegator = new MqClientImpl();
  }
  
  /**
   * Connect client to the KAS/MQ server.
   * 
   * @param host The host name or IP address
   * @param port The port number
   * @param user The user's name
   * @param pwd The user's password
   */
  public void connect(String host, int port, String user, String pwd) throws KasException
  {
    mLogger.debug("MqClient::connect() - IN, Host=" + host + ", Port=" + port + ", User=" + user + ", Pwd=" + pwd);
    
    mDelegator.connect(host, port, user, pwd);
    if (!isConnected())
      throw new KasException("Error - connect() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqClient::connect() - OUT");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.
   */
  public void disconnect() throws KasException
  {
    mLogger.debug("MqClient::disconnect() - IN");
    
    mDelegator.disconnect();
    if (isConnected())
      throw new KasException("Error - disconnect() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqClient::disconnect() - OUT");
  }

  /**
   * Get the connection status.
   * 
   * @return {@code true} if client is connected, {@code false} otherwise
   */
  public boolean isConnected()
  {
    return mDelegator.isConnected();
  }
  
  /**
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   */
  public void open(String queue) throws KasException
  {
    mLogger.debug("MqClient::open() - IN, Queue=" + queue);
    
    mDelegator.open(queue);
    if (!mDelegator.isOpen())
      throw new KasException("Error - open() failed. Client response: " + mDelegator.getResponse()); 
    
    mLogger.debug("MqClient::open() - OUT");
  }
  
  /**
   * Close opened queue.
   */
  public void close() throws KasException
  {
    mLogger.debug("MqClient::close() - IN");

    mDelegator.close();
    if (mDelegator.isOpen())
      throw new KasException("Error - close() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqClient::close() - OUT");
  }
  
  /**
   * Get the opened queue status
   * 
   * @return {@code true} if a queue is open, {@code false} otherwise
   */
  public boolean isOpen()
  {
    return mDelegator.isOpen();
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public boolean define(String queue)
  {
    mLogger.debug("MqClient::define() - IN, Queue=" + queue);
    
    boolean success = mDelegator.define(queue);
    
    mLogger.debug("MqClient::define() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   */
  public boolean delete(String queue)
  {
    mLogger.debug("MqClient::delete() - IN, Queue=" + queue);
    
    boolean success = mDelegator.delete(queue);
    
    mLogger.debug("MqClient::delete() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param priority The priority of the message to retrieve
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public IMqMessage<?> get(int priority, long timeout, long interval)
  {
    mLogger.debug("MqClient::get() - IN, Priority=" + priority + ", Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = mDelegator.get(priority, timeout, interval);
    
    mLogger.debug("MqClient::get() - OUT, Returns=" + result.toPrintableString(0));
    return result;
  }
  
  /**
   * Put a message into queue.
   * 
   * @param message The message to be put
   */
  public void put(IMqMessage<?> message)
  {
    mLogger.debug("MqClient::put() - IN, Message=" + message.toPrintableString(0));
    
    mDelegator.put(message);
    
    mLogger.debug("MqClient::put() - OUT");
  }
  
  /**
   * Switch login credentials
   * 
   * @param user The user's name
   * @param pwd The user's password
   * @return {@code true} if {@code password} matches the user's password as defined in {@link MqConfiguration},
   * {@code false} otherwise
   */
  public boolean login(String user, String pwd)
  {
    mLogger.debug("MqClient::login() - IN, User=" + user + ", Pwd=" + pwd);
    
    boolean success = mDelegator.login(user, pwd);
    
    mLogger.debug("MqClient::login() - OUT, Returns=" + success);
    return success;
  }
}
