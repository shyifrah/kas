package com.kas.mq.impl;

import com.kas.infra.base.KasException;
import com.kas.infra.utils.StringUtils;
import com.kas.infra.utils.Validators;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.client.IClient;
import com.kas.mq.client.MqClientImpl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;

/**
 * A KAS/MQ client
 * 
 * @author Pippo
 */
public final class MqContext implements IClient
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
  public MqContext()
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
   * 
   * @throws KasException if client failed to connect to KAS/MQ server
   */
  public void connect(String host, int port, String user, String pwd) throws KasException
  {
    mLogger.debug("MqContext::connect() - IN, Host=" + host + ", Port=" + port + ", User=" + user + ", Pwd=" + pwd);
    
    if (!Validators.isHostName(host) && !Validators.isIpAddress(host))
      throw new KasException("Validation failed. \"" + host + "\" is neither a valid host name nor a valid IP address");
    if (!Validators.isPort(port))
      throw new KasException("Validation failed. \"" + port + "\" is not a valid port number");
    if (!Validators.isUserName(user))
      throw new KasException("Validation failed. \"" + user + "\" is not a valid user name");
    
    
    mDelegator.connect(host, port, user, pwd);
    if (!isConnected())
      throw new KasException("Error - connect() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqContext::connect() - OUT");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.<br>
   * <br>
   * If client was not previously connected, this method has no effect
   * 
   * @throws KasException if client failed to disconnect from KAS/MQ server
   */
  public void disconnect() throws KasException
  {
    mLogger.debug("MqContext::disconnect() - IN");
    
    mDelegator.disconnect();
    if (isConnected())
      throw new KasException("Error - disconnect() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqContext::disconnect() - OUT");
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
   * 
   * @throws KasException if client failed to open {@code queue}
   */
  public void open(String queue) throws KasException
  {
    mLogger.debug("MqContext::open() - IN, Queue=" + queue);
    
    if (!Validators.isQueueName(queue))
      throw new KasException("Validation failed. \"" + queue + "\" is not a valid queue name");
    
    mDelegator.open(queue);
    if (!mDelegator.isOpen())
      throw new KasException("Error - open() failed. Client response: " + mDelegator.getResponse()); 
    
    mLogger.debug("MqContext::open() - OUT");
  }
  
  /**
   * Close opened queue.<br>
   * <br>
   * If no queue was previously opened, this method has no effect
   * 
   * @throws KasException if client failed to close the opened queue
   */
  public void close() throws KasException
  {
    mLogger.debug("MqContext::close() - IN");

    mDelegator.close();
    if (mDelegator.isOpen())
      throw new KasException("Error - close() failed. Client response: " + mDelegator.getResponse());
    
    mLogger.debug("MqContext::close() - OUT");
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
    mLogger.debug("MqContext::define() - IN, Queue=" + queue);
    
    boolean success = false;
    if (Validators.isQueueName(queue))
    {
      success = mDelegator.define(queue);
    }
    else
    {
      setResponse("Failed to define queue, invalid queue name: " + queue);
    }
    
    mLogger.debug("MqContext::define() - OUT, Returns=" + success);
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
    mLogger.debug("MqContext::delete() - IN, Queue=" + queue);
    
    boolean success = false;
    if (Validators.isQueueName(queue))
    {
      success = mDelegator.delete(queue);
    }
    else
    {
      setResponse("Failed to delete queue, invalid queue name: " + queue);
    }
    
    mLogger.debug("MqContext::delete() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param timeout The number of milliseconds to wait until a message available. A value of 0 means to wait indefinitely.
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public IMqMessage<?> get(long timeout, long interval)
  {
    mLogger.debug("MqContext::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = mDelegator.get(timeout, interval);
    
    mLogger.debug("MqContext::get() - OUT, Returns=" + StringUtils.asPrintableString(result));
    return result;
  }
  
  /**
   * Put a message into queue.
   * 
   * @param message The message to be put
   */
  public void put(IMqMessage<?> message)
  {
    mLogger.debug("MqContext::put() - IN, Message=" + StringUtils.asPrintableString(message));
    
    mDelegator.put(message);
    
    mLogger.debug("MqContext::put() - OUT");
  }
  
  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqContext::put() - IN");
    
    boolean success = mDelegator.shutdown();
    
    mLogger.debug("MqContext::put() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   */
  public String getResponse()
  {
    return mDelegator.getResponse();
  }

  /**
   * Set {@link IClient} response to {@code response}.
   * 
   * @param response The text that will be saved for {@link #getResponse} call
   */
  public void setResponse(String response)
  {
    mDelegator.setResponse(response);
  }
}
