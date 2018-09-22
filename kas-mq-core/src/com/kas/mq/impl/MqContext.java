package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.infra.utils.Validators;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.AMqMessage;

/**
 * A KAS/MQ context is basically a KAS/MQ client, providing all basic
 * functionality a client will need in order to exploit KAS/MQ services.
 * 
 * @author Pippo
 */
public final class MqContext extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * The actual client
   */
  private MqContextConnection mConnection = new MqContextConnection();
  
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
   * @see com.kas.mq.impl.internal.IClient#connect(String, int, String, String)
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
    
    
    mConnection.connect(host, port);
    if (!isConnected())
      throw new KasException("Error - connect() failed. Client response: " + mConnection.getResponse());
    
    boolean authenticated = mConnection.login(user, pwd);
    if (!authenticated)
      throw new KasException("Error - login() failed. Client response: " + mConnection.getResponse());
    
    mLogger.debug("MqContext::connect() - OUT");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.<br>
   * <br>
   * If client was not previously connected, this method has no effect
   * 
   * @throws KasException if client failed to disconnect from KAS/MQ server
   * 
   * @see com.kas.mq.impl.internal.IClient#disconnect()
   */
  public void disconnect() throws KasException
  {
    mLogger.debug("MqContext::disconnect() - IN");
    
    mConnection.disconnect();
    if (isConnected())
      throw new KasException("Error - disconnect() failed. Client response: " + mConnection.getResponse());
    
    mLogger.debug("MqContext::disconnect() - OUT");
  }

  /**
   * Get the connection status.
   * 
   * @return {@code true} if client is connected, {@code false} otherwise
   * 
   * @see com.kas.mq.impl.internal.IClient#isConnected()
   */
  public boolean isConnected()
  {
    return mConnection.isConnected();
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   * 
   * @see com.kas.mq.impl.internal.IClient#defineQueue(String, int)
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
      success = mConnection.defineQueue(queue, threshold);
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
   * @see com.kas.mq.impl.internal.IClient#delete(String)
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.debug("MqContext::deleteQueue() - IN, Queue=" + queue);
    
    boolean success = false;
    if (Validators.isQueueName(queue))
    {
      success = mConnection.deleteQueue(queue, force);
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
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @return the number of records returned that matched the query, or -1 if an error occurred
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean)
   */
  public Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("MqContext::queryQueue() - IN, Queue=" + name);
    
    Properties result = null;
    if (Validators.isQueueName(name))
    {
      result = mConnection.queryQueue(name, prefix, all);
    }
    else
    {
      setResponse("Query failed, invalid queue name" + (prefix ? " prefix: " : ": ") + ":" + name);
    }
    
    mLogger.debug("MqContext::queryQueue() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available. A value of 0 means to wait indefinitely.
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link AMqMessage} object or {@code null} if a message is unavailable
   * 
   * @see com.kas.mq.impl.internal.IClient#get(String, long, long)
   */
  public IMqMessage<?> get(String queue, long timeout, long interval)
  {
    mLogger.debug("MqContext::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = null;
    if (Validators.isQueueName(queue))
    {
      result = mConnection.get(queue, timeout, interval);
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
   * @see com.kas.mq.impl.internal.IClient#put(String, IMqMessage)
   */
  public void put(String queue, IMqMessage<?> message)
  {
    mLogger.debug("MqContext::put() - IN, Message=" + StringUtils.asPrintableString(message));
    
    if (Validators.isQueueName(queue))
    {
      mConnection.put(queue, message);
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
    
    boolean success = mConnection.shutdown();
    
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
    return mConnection.getResponse();
  }

  /**
   * Set {@link IClient} response to {@code response}.
   * 
   * @param response The text that will be saved for {@link #getResponse} call
   */
  public void setResponse(String response)
  {
    mConnection.setResponse(response);
  }
  
  /**
   * Get string representation of the object
   * 
   * @return string representation of the object
   */
  public String toString()
  {
    return mConnection.toString();
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
      .append(pad).append("  Connection=(").append(mConnection.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
