package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.StringUtils;
import com.kas.infra.utils.Validators;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.client.IClient;
import com.kas.mq.client.MqClientImpl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.AMqMessage;

/**
 * A KAS/MQ client
 * 
 * @author Pippo
 */
public final class MqContext extends AKasObject implements IClient
{
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * The actual client
   */
  private MqClientImpl mDelegator = new MqClientImpl();
  
  /**
   * Connect client to the KAS/MQ server.
   * 
   * @param host The host name or IP address
   * @param port The port number
   * @param user The user's name
   * @param pwd The user's password
   * 
   * @throws KasException if client failed to connect to KAS/MQ server
   * 
   * @see com.kas.mq.client.IClient#connect(String, int, String, String)
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
   * 
   * @see com.kas.mq.client.IClient#disconnect()
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
   * 
   * @see com.kas.mq.client.IClient#isConnected()
   */
  public boolean isConnected()
  {
    return mDelegator.isConnected();
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#defineQueue(String, int)
   */
  public boolean defineQueue(String queue, int threshold)
  {
    mLogger.debug("MqContext::defineQueue() - IN, Queue=" + queue);
    
    boolean success = false;
    if (!Validators.isQueueName(queue))
    {
      setResponse("Failed to define queue, invalid queue name: " + queue);
      
    }
    else if (threshold <= 0)
    {
      setResponse("Failed to define queue, invalid threshold: " + threshold);
    }
    else
    {
      success = mDelegator.defineQueue(queue, threshold);
    }
    
    mLogger.debug("MqContext::defineQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#delete(String)
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.debug("MqContext::deleteQueue() - IN, Queue=" + queue);
    
    boolean success = false;
    if (Validators.isQueueName(queue))
    {
      success = mDelegator.deleteQueue(queue, force);
    }
    else
    {
      setResponse("Failed to delete queue, invalid queue name: " + queue);
    }
    
    mLogger.debug("MqContext::deleteQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param prefix The queue name prefix
   * @param all if {@code true}, display all information on all queues 
   * @return {@code true} if query command was successful, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#queryQueue(String, boolean)
   */
  public boolean queryQueue(String prefix, boolean all)
  {
    mLogger.debug("MqContext::queryQueue() - IN, Queue=" + prefix);
    
    boolean success = false;
    if (Validators.isQueueName(prefix))
    {
      success = mDelegator.queryQueue(prefix, all);
    }
    else
    {
      setResponse("Query failed, invalid queue name prefix: " + prefix);
    }
    
    mLogger.debug("MqContext::queryQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available. A value of 0 means to wait indefinitely.
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link AMqMessage} object or {@code null} if a message is unavailable
   * 
   * @see com.kas.mq.client.IClient#get(String, long, long)
   */
  public IMqMessage<?> get(String queue, long timeout, long interval)
  {
    mLogger.debug("MqContext::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = null;
    if (Validators.isQueueName(queue))
    {
      result = mDelegator.get(queue, timeout, interval);
    }
    else
    {
      setResponse("Failed to get message, invalid queue name: " + queue);
    }
    
    mLogger.debug("MqContext::get() - OUT, Returns=" + StringUtils.asPrintableString(result));
    return result;
  }
  
  /**
   * Put a message into queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   * 
   * @see com.kas.mq.client.IClient#put(String, IMqMessage)
   */
  public void put(String queue, IMqMessage<?> message)
  {
    mLogger.debug("MqContext::put() - IN, Message=" + StringUtils.asPrintableString(message));
    
    if (Validators.isQueueName(queue))
    {
      mDelegator.put(queue, message);
    }
    else
    {
      setResponse("Failed to put message, invalid queue name: " + queue);
    }
    
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
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ClientImpl=(").append(mDelegator.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
