package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqResponse;
import com.kas.mq.impl.EMqResponseCode;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessageFactory;

/**
 * A client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 */
public class MqClientImpl extends AKasObject implements IClient
{
  /**
   * Messenger
   */
  private IMessenger mMessenger;
  
  /**
   * A logger
   */
  private ILogger mLogger;
  
  /**
   * Active user
   */
  private String mUser;
  
  /**
   * The response from last call
   */
  private String mResponse;
  
  /**
   * Constructing the client
   */
  public MqClientImpl()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mMessenger = null;
  }
  
  /**
   * Connect client to the KAS/MQ server.<br>
   * <br>
   * If the {@link MqClientImpl} is already connected, it will be disconnected first.
   * 
   * @param host The host name or IP address (uppercased)
   * @param port The port number
   * @param user The user's name (uppercased)
   * @param pwd The user's password
   * 
   * @see com.kas.mq.client.IClient#connect(String, int, String, String)
   */
  public void connect(String host, int port, String user, String pwd)
  {
    mLogger.debug("MqClientImpl::connect() - IN");
    
    if (isConnected()) disconnect();
    
    try
    {
      mMessenger = MessengerFactory.create(host, port);
      boolean authenticated = login(user, pwd);
      if (!authenticated)
      {
        logErrorAndSetResponse(getResponse());
        mMessenger.cleanup();
        mMessenger = null;
      }
      else
      {
        logInfoAndSetResponse("Connection established with host at " + mMessenger.getAddress());
        mUser = user;
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [")
        .append(new NetworkAddress(host, port)).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::connect() - OUT");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.
   * <br>
   * First we verify the client is actually connected, otherwise there's no point in disconnecting.<br>
   * Note we allocate a new {@link Socket} following the call to {@link Socket#close() close()} because
   * a closed socket cannot be reused.
   * 
   * @throws KasException if client failed to disconnect from KAS/MQ server
   * 
   * @see com.kas.mq.client.IClient#disconnect()
   */
  public void disconnect()
  {
    mLogger.debug("MqClientImpl::disconnect() - IN");
    
    if (!isConnected())
    {
      logInfoAndSetResponse("Not connected");
    }
    else
    {
      NetworkAddress addr = mMessenger.getAddress();
      mMessenger.cleanup();
      
      logInfoAndSetResponse("Connection terminated with " + addr.toString());
      
      mMessenger = null;
      mUser = null;
    }
    
    mLogger.debug("MqClientImpl::disconnect() - OUT");
  }

  /**
   * Get the connection status.<br>
   * <br>
   * A connected socket is one that is marked as connected AND not closed.
   * 
   * @return {@code true} if socket is connected and not closed, {@code false} otherwise
   * 
   * @see java.net.Socket#isConnected()
   * @see com.kas.mq.client.IClient#isConnected()
   */
  public boolean isConnected()
  {
    return mMessenger == null ? false : mMessenger.isConnected();
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#defineQueue(String,int)
   */
  public boolean defineQueue(String queue, int threshold)
  {
    mLogger.debug("MqClientImpl::defineQueue() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createDefineQueueRequest(queue, threshold);
      mLogger.debug("MqClientImpl::defineQueue() - sending define request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::defineQueue() - received response: " + response.toPrintableString());
        if ((response.getCode() == EMqResponseCode.cOkay) || (response.getCode() == EMqResponseCode.cWarn))
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully defined");
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to define queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::defineQueue() - OUT, Returns=" + success);
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
    mLogger.debug("MqClientImpl::deleteQueue() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createDeleteQueueRequest(queue, force);
      mLogger.debug("MqClientImpl::deleteQueue() - sending delete request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::deleteQueue() - received response: " + response.toPrintableString());
        if ((response.getCode() == EMqResponseCode.cOkay) || (response.getCode() == EMqResponseCode.cWarn))
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully deleted");
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to delete queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::deleteQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If ends with {@code asterisk}, then the name is a prefix
   * @param all if {@code true}, display all information on all queues 
   * @return {@code true} if query command was successful, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#queryQueue(String, boolean)
   */
  public boolean queryQueue(String name, boolean all)
  {
    mLogger.debug("MqClientImpl::queryQueue() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createQueryQueueRequest(name, all);
      mLogger.debug("MqClientImpl::queryQueue() - sending query queue request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::queryQueue() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
          success = true;
        
        logInfoAndSetResponse(response.getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to query KAS/MQ server for queues prefixed with ")
          .append(name).append(". Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::queryQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   * 
   * @see com.kas.mq.client.IClient#get(String, long, long)
   */
  public IMqMessage<?> get(String queue, long timeout, long interval)
  {
    mLogger.debug("MqClientImpl::get() - IN");
    
    IMqMessage<?> result = null;
    
    try
    {
      IMqMessage<?> request = MqMessageFactory.createGetRequest(queue, timeout, interval);
      mLogger.debug("MqClientImpl::get() - sending get request: " + request.toPrintableString(0));
      IPacket packet = mMessenger.sendAndReceive(request);
      IMqMessage<?> responseMessage = (IMqMessage<?>)packet;
      MqResponse response = new MqResponse(responseMessage);
      mLogger.debug("MqClientImpl::get() - received response: " + response.toPrintableString());
      if (response.getCode() == EMqResponseCode.cOkay)
      {
        responseMessage.setStringProperty(IMqConstants.cKasPropertyGetUserName, mUser);
        responseMessage.setStringProperty(IMqConstants.cKasPropertyGetTimeStamp, TimeStamp.nowAsString());
        logInfoAndSetResponse("Successfully got a message from queue " + queue + ", Message: " + responseMessage.toPrintableString(0));
        result = responseMessage;
      }
      else
      {
        logInfoAndSetResponse(response.getDesc());
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to get a message from queue [")
        .append(queue).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::get() - OUT");
    return result;
  }
  
  /**
   * Put a message into the opened queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   * 
   * @see com.kas.mq.client.IClient#put(String, IMqMessage)
   */
  public void put(String queue, IMqMessage<?> message)
  {
    mLogger.debug("MqClientImpl::put() - IN");
    
    try
    {
      message.setStringProperty(IMqConstants.cKasPropertyPutQueueName, queue);
      message.setStringProperty(IMqConstants.cKasPropertyPutUserName, mUser);
      message.setStringProperty(IMqConstants.cKasPropertyPutTimeStamp, TimeStamp.nowAsString());
      mMessenger.send(message);
      logDebugAndSetResponse("put", "Put to queue " + queue + " ended successfully. message: " + message.toPrintableString(0));
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to put a message into queue [")
        .append(queue).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::put() - OUT");
  }
  
  /**
   * login to KAS/MQ server.
   * 
   * @param user The user's name
   * @param pwd The user's password
   * @return {@code true} if {@code password} matches the user's password as defined in {@link MqConfiguration},
   * {@code false} otherwise
   */
  public boolean login(String user, String pwd)
  {
    mLogger.debug("MqClientImpl::login() - IN");
    
    boolean success = false;
    IMqMessage<?> request = MqMessageFactory.createAuthenticationRequest(user, pwd);
    mLogger.debug("MqClientImpl::login() - sending authentication request: " + request.toPrintableString(0));
    try
    {
      IPacket packet = mMessenger.sendAndReceive(request);
      MqResponse response = new MqResponse((IMqMessage<?>)packet);
      mLogger.debug("MqClientImpl::login() - received response: " + response.toPrintableString());
      if (response.getCode() == EMqResponseCode.cOkay)
      {
        success = true;
        logInfoAndSetResponse("User " + user + " successfully authenticated");
        mUser = user;
      }
      else
      {
        logInfoAndSetResponse(response.getDesc());
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred during authentication of user [")
        .append(user).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::login() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#shutdown()
   */
  public boolean shutdown()
  {
    mLogger.debug("MqClientImpl::shutdown() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createShutdownRequest();
      mLogger.debug("MqClientImpl::shutdown() - sending shutdown request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::shutdown() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          success = true;
          logInfoAndSetResponse("Shutdown request was posted");
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to signal KAS/MQ server to shutdown. Exception: ")
          .append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::shutdown() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   * 
   * @see com.kas.mq.client.IClient#getResponse()
   */
  public String getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   * 
   * @see com.kas.mq.client.IClient#getResponse()
   */
  public void setResponse(String response)
  {
    mResponse = response;
  }
  
  /**
   * Log DEBUG a message
   * 
   * @param message The message to log and set as the client's response
   */
  private void logDebugAndSetResponse(String method, String message)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName())
      .append("::")
      .append(method)
      .append("() - ")
      .append(message);
    
    mLogger.debug(sb.toString());
    setResponse(message);
  }
  
  /**
   * Log INFO a message
   * 
   * @param message The message to log and set as the client's response
   */
  private void logInfoAndSetResponse(String message)
  {
    mLogger.info(message);
    setResponse(message);
  }
  
  /**
   * Log ERROR a message
   * 
   * @param message The message to log and set as the client's response
   */
  private void logErrorAndSetResponse(String message)
  {
    mLogger.error(message);
    setResponse(message);
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
    return name();
  }
}
