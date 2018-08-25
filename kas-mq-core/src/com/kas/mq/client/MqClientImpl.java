package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqResponse;
import com.kas.mq.impl.EMqResponseCode;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;
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
   * Target queue
   */
  private String mQueue;
  
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
   * @param host The host name or IP address
   * @param port The port number
   * @param user The user's name
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
   * Disconnecting from the remote KAS/MQ server.<br>
   * <br>
   * First we verify the client is actually connected, otherwise there's no point in disconnecting.<br>
   * Note we allocate a new {@link Socket} following the call to {@link Socket#close() close()} because
   * a closed socket cannot be reused.
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
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   * 
   * @see com.kas.mq.client.IClient#open(String)
   */
  public void open(String queue)
  {
    mLogger.debug("MqClientImpl::open() - IN");
    
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      if (isOpen()) close();
      
      IMqMessage<?> request = MqMessageFactory.createOpenRequest(queue);
      mLogger.debug("MqClientImpl::open() - sending open request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::open() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          logInfoAndSetResponse("Queue " + queue + " was successfully opened");
          mQueue = queue;
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to open queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::open() - OUT");
  }
  
  /**
   * Close opened queue.
   * 
   * @see com.kas.mq.client.IClient#close()
   */
  public void close()
  {
    mLogger.debug("MqClientImpl::close() - IN");
    String queue = mQueue;
    
    if (!isConnected())
    {
      logInfoAndSetResponse("Not connected to host");
    }
    else if (!isOpen())
    {
      logInfoAndSetResponse("Queue is not open");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createCloseRequest(queue);
      mLogger.debug("MqClientImpl::close() - sending close request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::close() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          logInfoAndSetResponse("Queue " + queue + " was successfully closed");
          mQueue = null;
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to close queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::close() - OUT");
  }
  
  /**
   * Get the opened queue status
   * 
   * @return {@code true} if the client has already opened a queue, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#isOpened()
   */
  public boolean isOpen()
  {
    return mQueue != null;
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @return the {@code true} if queue was defined, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#define(String)
   */
  public boolean define(String queue)
  {
    mLogger.debug("MqClientImpl::define() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createDefineRequest(queue);
      mLogger.debug("MqClientImpl::define() - sending define request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::define() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully defined");
          mQueue = queue;
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
    
    mLogger.debug("MqClientImpl::define() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   * 
   * @see com.kas.mq.client.IClient#delete(String)
   */
  public boolean delete(String queue)
  {
    mLogger.debug("MqClientImpl::delete() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqMessageFactory.createDeleteRequest(queue);
      mLogger.debug("MqClientImpl::delete() - sending delete request: " + request.toPrintableString(0));
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::delete() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully deleted");
          if ((mQueue != null) && (mQueue.equals(queue)))
            mQueue = queue;
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
    
    mLogger.debug("MqClientImpl::delete() - OUT, Returns=" + success);
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
    mLogger.debug("MqClientImpl::get() - IN");
    
    IMqMessage<?> result = null;
    
    if (!isOpen())
    {
      logDebugAndSetResponse("MqClientImpl::get()", "Cannot get a message. Need to open a queue first");
    }
    else
    {
      try
      {
        IMqMessage<?> request = MqMessageFactory.createGetRequest(priority, timeout, interval);
        mLogger.debug("MqClientImpl::get() - sending get request: " + request.toPrintableString(0));
        IPacket packet = mMessenger.sendAndReceive(request);
        IMqMessage<?> responseMessage = (IMqMessage<?>)packet;
        MqResponse response = new MqResponse(responseMessage);
        mLogger.debug("MqClientImpl::get() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          responseMessage.setStringProperty(IMqConstants.cKasPropertyGetUserName, mUser);
          responseMessage.setStringProperty(IMqConstants.cKasPropertyGetTimeStamp, TimeStamp.nowAsString());
          logInfoAndSetResponse("Successfully got a message from queue " + mQueue + ", Message: " + responseMessage.toPrintableString(0));
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
          .append(mQueue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::get() - OUT");
    return result;
  }
  
  /**
   * Put a message into queue.<br>
   * <br>
   * The message is basically handled by the server side, except for the target queue name, which must be
   * filled prior to sending it.
   * 
   * @param message The message to be put
   */
  public void put(IMqMessage<?> message)
  {
    mLogger.debug("MqClientImpl::put() - IN");
    
    if (!isOpen())
    {
      logDebugAndSetResponse("MqClientImpl::put()", "Cannot put a message. Need to open a queue first");
    }
    else
    {
      try
      {
        message.setStringProperty(IMqConstants.cKasPropertyQueueName, mQueue);
        message.setStringProperty(IMqConstants.cKasPropertyPutUserName, mUser);
        message.setStringProperty(IMqConstants.cKasPropertyPutTimeStamp, TimeStamp.nowAsString());
        IPacket packet = mMessenger.sendAndReceive(message);
        MqResponse response = new MqResponse((IMqMessage<?>)packet);
        mLogger.debug("MqClientImpl::put() - received response: " + response.toPrintableString());
        if (response.getCode() == EMqResponseCode.cOkay)
        {
          logInfoAndSetResponse("Successfully put to queue " + mQueue + " message: " + message.toPrintableString(0));
        }
        else
        {
          logInfoAndSetResponse(response.getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to put a message into queue [")
          .append(mQueue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::put() - OUT");
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
   * Log INFO a message
   * 
   * @param message The message to log and set as the client's response
   */
  private void logDebugAndSetResponse(String where, String message)
  {
    mLogger.debug(where + " - " + message);
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
