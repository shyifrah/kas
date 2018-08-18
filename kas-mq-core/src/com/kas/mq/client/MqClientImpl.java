package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.ThrowableFormatter;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponseMessage;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;

/**
 * A client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 *
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
    
    if (isConnected())
    {
      disconnect();
    }
    
    try
    {
      mMessenger = MessengerFactory.create(host, port);
      boolean authenticated = authenticate(user, pwd);
      if (!authenticated)
      {
        String message = "User name \"" + user + "\" failed authentication";
        mLogger.error(message);
        setResponse(message);
        mMessenger.cleanup();
      }
      else
      {
        String message = "Connection established with host at " + mMessenger.getAddress();
        mLogger.info(message);
        setResponse(message);
      }
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [")
        .append(new NetworkAddress(host, port))
        .append("]. Exception: ")
        .append(new ThrowableFormatter(e).toString());
      String message = sb.toString();
      mLogger.error(message);
      setResponse(message);
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
      String message = "Not Connected";
      mLogger.info(message);
      setResponse(message);
    }
    else
    {
      NetworkAddress addr = mMessenger.getAddress();
      mMessenger.cleanup();
      
      String message = "Connection terminated with " + addr.toString();
      setResponse(message);
      mLogger.info(message);
      
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
  public boolean isConnected()
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
  public NetworkAddress getNetworkAddress()
  {
    return mMessenger == null ? null : mMessenger.getAddress();
  }
  
  /**
   * Open a queue
   */
  public MqQueue open(String queue)
  {
    return null;
  }

  public void close()
  {
  }

  public MqMessage createMessage()
  {
    return null;
  }

  public MqMessage get()
  {
    return null;
  }

  public MqMessage getAndWait()
  {
    return null;
  }

  public MqMessage getAndWaitWithTimeout(long timeout)
  {
    return null;
  }

  public void put(MqMessage message)
  {
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
    try
    {
      IPacket packet = mMessenger.sendAndReceive(request);
      MqResponseMessage response = (MqResponseMessage)packet;
      if (response.getResponseCode() == 0)
      {
        success = true;
      }
      else
      {
        String message = response.getResponseMessage();
        setResponse(message);
        mLogger.info(message);
      }
    }
    catch (Throwable e) {}
    
    mLogger.debug("MqClientImpl::authenticate() - OUT, Returns=" + success);
    return success;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
