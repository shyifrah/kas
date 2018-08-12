package com.kas.mq.client;

import java.io.IOException;
import java.net.Socket;
import com.kas.infra.base.ThrowableFormatter;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqMessage;

/**
 * A client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 *
 */
public class MqClientImpl extends AMqClient
{
  /**
   * The socket used for incoming/outgoing traffic between the server and the client
   */
  private Socket mSocket;
  
  /**
   * A logger
   */
  private ILogger mLogger;
  
  /**
   * Constructing the client
   */
  public MqClientImpl()
  {
    mSocket = new Socket();
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /**
   * Connecting to the KAS/MQ server.<br>
   * <br>
   * If the {@link MqClientImpl} is already connected, we need to disconnect first.
   * 
   * @param host The host name or IP address of the remote host
   * @param port The port on which the KAS/MQ server is listening for new incoming connections.
   */
  public void connect(String host, int port)
  {
    if (isConnected())
    {
      disconnect();
    }
    
    try
    {
      mSocket = new Socket(host, port);
      String message = "Connection established with host at [" + host + ':' + port + "]";
      mLogger.info(message);
      setResponse(message);
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [")
        .append(host)
        .append(':')
        .append(port)
        .append("]. Exception: ")
        .append(new ThrowableFormatter(e).toString());
      String message = sb.toString();
      mLogger.error(message);
      setResponse(message);
    }
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
    String message;
    if (!isConnected())
    {
      message = "Not connected";
    }
    else
    {
      try
      {
        mSocket.close();
      }
      catch (IOException e)
      {
        mLogger.warn("Exception occurred while trying to close socket [" + mSocket.toString() + "]", e);
      }
      mSocket = new Socket();
      message = "Connection with remote host terminated";
    }
    setResponse(message);
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
    return mSocket == null ? false : mSocket.isConnected() && !mSocket.isClosed();
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

  public String toPrintableString(int level)
  {
    return null;
  }
}
