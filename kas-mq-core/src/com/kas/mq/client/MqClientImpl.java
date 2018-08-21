package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqResponseMessage;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqQueue;

/**
 * A client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 */
public class MqClientImpl extends AMqClient
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
      boolean authenticated = authenticate(user, pwd);
      if (!authenticated)
      {
        logErrorAndSetResponse("User name \"" + user + "\" failed authentication");
        mMessenger.cleanup();
      }
      else
      {
        logInfoAndSetResponse("Connection established with host at " + mMessenger.getAddress());
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [").append(new NetworkAddress(host, port))
        .append("]. Exception: ").append(StringUtils.format(e));
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
   */
  private boolean isConnected()
  {
    return mMessenger == null ? false : mMessenger.isConnected();
  }
  
  /**
   * Get the {@link NetworkAddress} for this {@link IClient} object
   * 
   * @return the {@link NetworkAddress} for this {@link IClient} object. If the {@link Socket}
   * is {@code null}, a {@code null} value is returned
   * 
   * @see com.kas.mq.client.IClient#getNetworkAddress()
   */
  private NetworkAddress getNetworkAddress()
  {
    return mMessenger == null ? null : mMessenger.getAddress();
  }
  
  /**
   * Open the specified queue.
   * 
   * @param queue The queue name to open.
   * @return the {@link MqQueue} object if queue was opened, {@code null} otherwise
   * 
   * @see com.kas.mq.client.IClient#open(String)
   */
  public boolean open(String queue)
  {
    mLogger.debug("MqClientImpl::open() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      if (isOpen()) close();
      
      MqMessage request = MqMessageFactory.createOpenRequest(queue);
      mLogger.debug("MqClientImpl::open() - sending open request: " + request.toPrintableString());
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponseMessage response = (MqResponseMessage)packet;
        mLogger.debug("MqClientImpl::open() - received response: " + response.toPrintableString());
        if (response.getResponseCode() == 0)
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully opened");
          mQueue = queue;
        }
        else
        {
          logInfoAndSetResponse(response.getResponseMessage());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to open queue [").append(queue)
          .append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::open() - OUT, Returns=" + success);
    return success;
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
      MqMessage request = MqMessageFactory.createCloseRequest(queue);
      mLogger.debug("MqClientImpl::close() - sending close request: " + request.toPrintableString());
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponseMessage response = (MqResponseMessage)packet;
        mLogger.debug("MqClientImpl::close() - received response: " + response.toPrintableString());
        if (response.getResponseCode() == 0)
        {
          logInfoAndSetResponse("Queue " + queue + " was successfully closed");
          mQueue = null;
        }
        else
        {
          logInfoAndSetResponse(response.getResponseMessage());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to close queue [").append(queue)
          .append("]. Exception: ").append(StringUtils.format(e));
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
  private boolean isOpen()
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
      MqMessage request = MqMessageFactory.createDefineRequest(queue);
      mLogger.debug("MqClientImpl::define() - sending define request: " + request.toPrintableString());
      try
      {
        IPacket packet = mMessenger.sendAndReceive(request);
        MqResponseMessage response = (MqResponseMessage)packet;
        mLogger.debug("MqClientImpl::define() - received response: " + response.toPrintableString());
        if (response.getResponseCode() == 0)
        {
          success = true;
          logInfoAndSetResponse("Queue " + queue + " was successfully defined");
          mQueue = queue;
        }
        else
        {
          logInfoAndSetResponse(response.getResponseMessage());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to define queue [").append(queue)
          .append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::define() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param timeout The number of milliseconds to wait until a message available
   * @return the {@link MqMessage} object or {@code null} if a message is unavailable
   */
  public MqMessage get(int timeout)
  {
    mLogger.debug("MqClientImpl::get() - IN");
    
    MqMessage result = null;
    
    if (!isOpen())
    {
      logInfoAndSetResponse("Cannot get a message. Need to open a queue first");
    }
    else
    {
      try
      {
        MqMessage request = MqMessageFactory.createGetRequest(mQueue);
        IPacket packet = mMessenger.sendAndReceive(request, timeout);
        MqResponseMessage response = (MqResponseMessage)packet;
        if (response.getResponseCode() == 0)
        {
          mLogger.debug("MqClientImpl::get() - Message received successfully. Message: " + response);
          result = response;
        }
        else
        {
          logInfoAndSetResponse(response.getResponseMessage());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to get a message from queue [").append(mQueue)
          .append("]. Exception: ").append(StringUtils.format(e));
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
  public void put(MqMessage message)
  {
    mLogger.debug("MqClientImpl::put() - IN");
    
    if (!isOpen())
    {
      String response = "Cannot put a message. Need to open a queue first";
      setResponse(response);
      mLogger.debug(response);
    }
    else
    {
      //message.setQueueName(mQueue);              ---+
      //message.setRequestType(ERequestType.cPut); ---+--> these two lines should probably be done prior to this point, by the caller
      try
      {
        mMessenger.send(message);
        mLogger.debug("Message with ID=(" + message.getMessageId() + ") was successfully put into queue " + mQueue);
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to put a message into queue [").append(mQueue)
          .append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::put() - OUT");
  }
  
  /**
   * Authenticate a user
   * 
   * @param username The user's name
   * @param password The user's password
   * @return {@code true} if {@code password} matches the user's password as defined in {@link MqConfiguration},
   * {@code false} otherwise
   */
  private boolean authenticate(String username, String password)
  {
    mLogger.debug("MqClientImpl::authenticate() - IN");
    
    boolean success = false;
    MqMessage request = MqMessageFactory.createAuthenticationRequest(username, password);
    mLogger.debug("MqClientImpl::authenticate() - sending authentication request: " + request.toPrintableString());
    try
    {
      IPacket packet = mMessenger.sendAndReceive(request);
      MqResponseMessage response = (MqResponseMessage)packet;
      mLogger.debug("MqClientImpl::authenticate() - received response: " + response.toPrintableString());
      if (response.getResponseCode() == 0)
      {
        success = true;
        mLogger.debug("MqClientImpl::authenticate() - client was successfully authenticated");
      }
      else
      {
        logInfoAndSetResponse(response.getResponseMessage());
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred during authentication of user [").append(username)
        .append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::authenticate() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Show session information
   */
  public void show()
  {
    mLogger.debug("MqClientImpl::show() - IN");
    
    StringBuilder sb = new StringBuilder('\n');
    
    MqMessage request = MqMessageFactory.createShowInfoRequest();
    mLogger.debug("MqClientImpl::show() - sending show-info request: " + request.toPrintableString());
    try
    {
      IPacket packet = mMessenger.sendAndReceive(request);
      MqResponseMessage response = (MqResponseMessage)packet;
      mLogger.debug("MqClientImpl::show() - received response: " + response.toPrintableString());
      if (response.getResponseCode() == 0)
      {
        NetworkAddress addr = getNetworkAddress();
        sb.append("Session ID.................: ").append(response.getStringProperty(IMqConstants.cKasPropertySessionId, "*N/A*")).append('\n');
        sb.append("    KAS/MQ server.......:    ").append(addr == null ? "*N/A*" : addr.toString()).append('\n');
        sb.append("    Client..............:    ").append(response.getStringProperty(IMqConstants.cKasPropertyNetworkAddress, "*N/A*")).append('\n');
        sb.append("    Connected as........:    ").append(response.getStringProperty(IMqConstants.cKasPropertyUserName, "*N/A*")).append('\n');
        sb.append("    Opened queue........:    ").append(response.getStringProperty(IMqConstants.cKasPropertyQueueName, "*N/A*"));
        String message = sb.toString();
        setResponse(message);
        mLogger.info(message);
      }
      else
      {
        logInfoAndSetResponse(response.getResponseMessage());
      }
    }
    catch (IOException e)
    {
      StringBuilder sbe = new StringBuilder();
      sbe.append("Exception occurred while getting session info. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sbe.toString());
    }
    
    mLogger.debug("MqClientImpl::show() - OUT");
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
