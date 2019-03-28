package com.kas.mq.internal;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqConnection;
import com.kas.mq.impl.messages.IMqMessage;

/**
 * A connection is the object that responsible for connecting, authenticating etc.
 * Basically, it wraps the simple functions of the {@link IMessenger}.
 * 
 * @author Pippo
 */
public class MqConnection extends AKasObject implements IMqConnection
{
  /**
   * A logger
   */
  protected Logger mLogger;
  
  /**
   * Messenger
   */
  protected IMessenger mMessenger;
  
  /**
   * Client Application name
   */
  protected String mClientName;
  
  /**
   * Active user.<br>
   * This data member is set after each successful {@link #login(String, String) login}.
   */
  protected String mUser = null;
  
  /**
   * Connection ID
   */
  private UniqueId mConnectionId;
  
  /**
   * The session ID the connection is serving
   */
  private UniqueId mSessionId = null;
  
  /**
   * The response from last call
   */
  private String mResponse;
  
  /**
   * Constructing the connection
   * 
   * @param clientName
   *   A name used by the client application
   */
  public MqConnection(String clientName)
  {
    mLogger = LogManager.getLogger(getClass());
    mConnectionId = UniqueId.generate();
    mMessenger = null;
    mClientName = clientName.toUpperCase();
  }
  
  /**
   * Connect client to the KAS/MQ server.<br>
   * If the {@link MqConnection} is already connected, it will be disconnected first.
   * 
   * @param host
   *   The host name or IP address (uppercased)
   * @param port
   *   The port number
   */
  public void connect(String host, int port)
  {
    mLogger.trace("MqConnection::connect() - IN");
    
    if (isConnected()) disconnect();
    
    NetworkAddress addr = new NetworkAddress(host, port);
    
    try
    {
      mMessenger = MessengerFactory.create(host, port);
      setResponse("Successfully connected to " + addr);
    }
    catch (ConnectException | UnknownHostException e)
    {
      logErrorAndSetResponse("Connection to [" + addr + "] refused");
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [")
        .append(addr).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
      
      if (mMessenger != null)
        mMessenger.cleanup();
    }
    
    mLogger.trace("MqConnection::connect() - OUT");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.
   * <br>
   * First we verify the client is actually connected, otherwise there's no point in disconnecting.<br>
   * Note we allocate a new {@link Socket} following the call to {@link Socket#close() close()} because
   * a closed socket cannot be reused.
   */
  public void disconnect()
  {
    mLogger.trace("MqConnection::disconnect() - IN");
    
    if (!isConnected())
    {
      logInfoAndSetResponse("Not connected to host");
    }
    else
    {
      NetworkAddress addr = mMessenger.getAddress();
      mMessenger.cleanup();
      
      logInfoAndSetResponse("Connection terminated with " + addr.toString());
      
      mUser = null;
      mSessionId = null;
    }
    
    mLogger.trace("MqConnection::disconnect() - OUT");
  }

  /**
   * Get the connection status.<br>
   * A connected socket is one that is marked as connected AND not closed.
   * 
   * @return
   *   {@code true} if socket is connected and not closed, {@code false} otherwise
   * 
   * @see java.net.Socket#isConnected()
   */
  public boolean isConnected()
  {
    return mMessenger == null ? false : mMessenger.isConnected();
  }
  
  /**
   * login to KAS/MQ server.
   * 
   * @param user
   *   The user's name
   * @param pwd
   *   The user's password
   * @return
   *   {@code true} if {@code password} matches the user's password
   */
  public boolean login(String user, String pwd)
  {
    mLogger.trace("MqConnection::login() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      String username = user.toUpperCase();
      IMqMessage request = MqRequestFactory.createLoginRequest(username, pwd, mClientName);
      mLogger.trace("MqConnection::login() - sending login request: " + StringUtils.asPrintableString(request));
      try
      {
        IMqMessage reply = (IMqMessage)mMessenger.sendAndReceive(request);
        mLogger.trace("MqConnection::login() - received response: " + StringUtils.asPrintableString(reply));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
        {
          String sid = reply.getStringProperty(IMqConstants.cKasPropertyLoginSession, UniqueId.cNullUniqueIdAsString);
          UniqueId uid = UniqueId.fromString(sid);
          success = true;
          mUser = username;
          mSessionId = uid;
        }
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred during login of user [")
          .append(user).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
        mMessenger.cleanup();
      }
    }
    
    mLogger.trace("MqConnection::login() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get a message from queue.
   * 
   * @param queue
   *   The target queue name
   * @param timeout
   *   The number of milliseconds to wait until a message available
   * @param interval
   *   The number in milliseconds the thread execution is suspended between each polling operation
   * @return
   *   the {@link IMqMessage} object or {@code null} if a message is unavailable
   */
  public IMqMessage get(String queue, long timeout, long interval)
  {
    mLogger.trace("MqConnection::get() - IN");
    
    IMqMessage result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      try
      {
        String qname = queue.toUpperCase();
        IMqMessage request = MqRequestFactory.createGetRequest(qname, timeout, interval);
        mLogger.trace("MqConnection::get() - sending get request: " + StringUtils.asPrintableString(request));
        IMqMessage reply = (IMqMessage)mMessenger.sendAndReceive(request);
        mLogger.trace("MqConnection::get() - received response: " + StringUtils.asPrintableString(reply));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
        {
          reply.setStringProperty(IMqConstants.cKasPropertyGetUserName, mUser);
          reply.setStringProperty(IMqConstants.cKasPropertyGetTimeStamp, TimeStamp.now().toString());
          result = reply;
          setResponse("Successfully got a message from queue " + queue + ", MessageID: " + reply.getMessageId());
        }
        else
        {
          logInfoAndSetResponse(reply.getResponse().getDesc());
        }
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to get a message from queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.trace("MqConnection::get() - OUT");
    return result;
  }
  
  /**
   * Put a message into the specified queue.
   * 
   * @param queue
   *   The target queue name
   * @param message
   *   The message to be put
   * @return
   *   the {@link IMqMessage} returned by the messenger
   */
  public IMqMessage put(String queue, IMqMessage message)
  {
    mLogger.trace("MqConnection::put() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      try
      {
        String qname = queue.toUpperCase();
        message.setStringProperty(IMqConstants.cKasPropertyPutQueueName, qname);
        message.setStringProperty(IMqConstants.cKasPropertyPutUserName, mUser.toUpperCase());
        message.setStringProperty(IMqConstants.cKasPropertyPutTimeStamp, TimeStamp.now().toString());
        
        mLogger.trace("MqConnection::put() - sending message: " + StringUtils.asPrintableString(message));
        reply = (IMqMessage)mMessenger.sendAndReceive(message);
        mLogger.trace("MqConnection::put() - received response: " + StringUtils.asPrintableString(reply));
        setResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to put a message into queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.trace("MqConnection::put() - OUT");
    return reply;
  }
  
  /**
   * Get the connection ID
   * 
   * @return
   *   the connection ID
   */
  public UniqueId getConnectionId()
  {
    return mConnectionId;
  }
  
  /**
   * Get last response from last {@link IMqConnection} call.
   * 
   * @return
   *   the last message the {@link IMqConnection} issued for a call.
   */
  public String getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set response from last {@link IMqConnection} call.
   * 
   * @param response
   *   The response from last {@link IMqConnection} call
   */
  public void setResponse(String response)
  {
    mResponse = response;
  }
  
  /**
   * Log INFO and set a message as the connection's response
   * 
   * @param message
   *   The message to log and set as the connection's response
   */
  protected void logInfoAndSetResponse(String message)
  {
    mLogger.info(message);
    setResponse(message);
  }
  
  /**
   * Log ERROR and set a message as the connection's response
   * 
   * @param message
   *   The message to log and set as the connection's response
   */
  protected void logErrorAndSetResponse(String message)
  {
    mLogger.error(message);
    setResponse(message);
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ConnectionId=").append(mConnectionId).append("\n")
      .append(pad).append("  ClientAppName=").append(mClientName).append("\n")
      .append(pad).append("  SessionId=").append(StringUtils.asString(mSessionId)).append("\n")
      .append(pad).append("  User=").append(StringUtils.asString(mUser)).append("\n")
      .append(pad).append("  Messenger=").append(StringUtils.asPrintableString(mMessenger, level+1)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
