package com.kas.mq.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.infra.utils.Validators;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqContextConnection;

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
  private MqContextConnection mConnection;
  
  /**
   * Construct the client
   * 
   * @param clientName The client application name
   */
  public MqContext(String clientName)
  {
    mConnection = new MqContextConnection(clientName);
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
   */
  public boolean isConnected()
  {
    return mConnection.isConnected();
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The name of the queue to define.
   * @param desc The queue description
   * @param threshold The queue threshold
   * @param disp The queue disposition
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public boolean defineQueue(String queue, String desc, int threshold, EQueueDisp disp)
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
      success = mConnection.defineQueue(queue, desc, threshold, disp);
    }
    
    mLogger.debug("MqContext::defineQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Alter a queue.
   * 
   * @param queue The queue name to alter
   * @param qProps The properties to alter for this queue 
   * @return the {@code true} if queue was altered, {@code false} otherwise
   */
  public boolean alterQueue(String queue, Properties qProps)
  {
    mLogger.debug("MqContext::alterQueue() - IN, Queue=" + queue);
    
    boolean success = false;  
    success = mConnection.alterQueue(queue, qProps);
      
    mLogger.debug("MqContext::alterQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return {@code true} if queue was deleted, {@code false} otherwise
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
   * Query KAS/MQ server for information
   * 
   * @param qType a {@link EQueryType} value that describes the type of query
   * @param qProps a {@link Properties} object used as query parameters for refining the query
   * @return the message returned by the KAS/MQ server
   */
  public MqStringMessage queryServer(EQueryType qType, Properties qProps)
  {
    mLogger.debug("MqContext::queryServer() - IN, QueryType=" + qType);
    
    MqStringMessage result = mConnection.queryServer(qType, qProps);
    
    mLogger.debug("MqContext::queryServer() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available. A value of 0 means to wait indefinitely.
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   */
  public IMqMessage get(String queue, long timeout, long interval)
  {
    mLogger.debug("MqContext::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage result = null;
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
   */
  public void put(String queue, IMqMessage message)
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
   * Get response from last call.
   * 
   * @return the response got from the last {@link MqConnection} call
   */
  public String getResponse()
  {
    return mConnection.getResponse();
  }

  /**
   * Set last response to {@code response}.
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
