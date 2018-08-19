package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponseMessage;
import com.kas.mq.internal.ERequestType;

/**
 * A {@link ClientHandler} is the object that handles the traffic in and from a remote client.
 * 
 * @author Pippo
 */
public class ClientHandler extends AKasObject implements Runnable
{
  /**
   * The client's unique ID
   */
  private UniqueId mClientId;
  
  /**
   * The client socket
   */
  private Socket mSocket;
  
  /**
   * Messenger
   */
  private IMessenger mMessenger;
  
  /**
   * The client controller
   */
  private IController mController;
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Client credentials
   */
  private String mUserName;
  
  /**
   * Client credentials
   */
  private MqQueue mOpenedQueue;
  
  /**
   * Indicator whether handler is still running
   */
  private boolean mIsRunning = true;
  
  /**
   * Construct a {@link ClientHandler} to handle all incoming and outgoing traffic of the client.<br>
   * <br>
   * Client's transmits messages and received by this handler over the specified {@code socket}.
   *  
   * @param socket The client's socket
   * 
   * @throws IOException if {@link Socket#setSoTimeout()} throws
   */
  ClientHandler(Socket socket, IController controller) throws IOException
  {
    mController = controller;
    mSocket = socket;
    mSocket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mMessenger = MessengerFactory.create(mSocket);
    
    mClientId = UniqueId.generate();
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /**
   * Running the handler - keep reading and process packets from input stream.<br>
   * If necessary, respond on output stream.
   */
  public void run()
  {
    mLogger.debug("ClientHandler::run() - IN");
    
    while (isRunning())
    {
      mLogger.debug("ClientHandler::run() - Waiting for messages from client...");
      try
      {
        IPacket packet = mMessenger.receive();
        if (packet != null)
        {
          mLogger.debug("ClientHandler::run() - Packet received: " + packet.toPrintableString(0));
          boolean success = process(packet);
          setRunningState(success);
        }
      }
      catch (SocketTimeoutException e)
      {
        mLogger.diag("ClientHandler::run() - Socket Timeout occurred. Resume waiting for a new packet from client...");
      }
      catch (SocketException e)
      {
        mLogger.info("Connection to remote host at " + new NetworkAddress(mSocket).toString() + " was dropped");
        stop();
      }
      catch (IOException e)
      {
        mLogger.error("An I/O error occurred while trying to receive packet from remote client. Exception: ", e);
        stop();
      }
    }
    
    mLogger.debug("ClientHandler::run() - OUT");
  }
  
  /**
   * Process received packet
   * 
   * @param packet The received packet
   * @return {@code true} if {@link ClientHandler} should continue processing next packet, {@code false} otherwise
   */
  private boolean process(IPacket packet)
  {
    mLogger.debug("ClientHandler::process() - IN");
    
    boolean success = true;
    MqResponseMessage response = null;
    int respCode = 0;
    String respMsg = "";
    try
    {
      MqMessage message = (MqMessage)packet;
      ERequestType requestType = message.getRequestType();
      if (requestType == ERequestType.cAuthenticate)
      {
        String user = message.getUserName();
        String pwd  = message.getPassword();
        
        if (!mController.isPasswordMatch(user, pwd))
        {
          respCode = 8;
          respMsg = "Password does not match";
          success = false;
        }
        else
        {
          mUserName = user;
        }
      }
      else if (requestType == ERequestType.cOpenQueue)
      {
        String queue = message.getQueueName();
        MqQueue mqq = mController.getQueue(queue);
        
        if (mqq == null)
        {
          respCode = 8;
          respMsg = "Queue does not exist";
          success = false;
        }
        else
        {
          mOpenedQueue = mqq;
        }
      }
      else if (requestType == ERequestType.cCloseQueue)
      {
        if (mOpenedQueue == null)
        {
          respCode = 4;
          respMsg = "No opened queue";
        }
        else
        {
          mOpenedQueue = null;
        }
      }
      
      response = MqMessageFactory.createResponse(respCode, respMsg);
      
      mLogger.debug("ClientHandler::process() - Responding with the message: " + response.toPrintableString());
      mMessenger.send(response);
    }
    catch (ClassCastException e)
    {
      mLogger.error("Invalid message received from remote client. Message: " + packet);
      success = false;
    }
    catch (IOException e)
    {
      mLogger.error("Failed to send response message to remote client. Message: " + StringUtils.asString(response));
      success = false;
    }
    
    mLogger.debug("ClientHandler::process() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Get the client unique ID
   * 
   * @return the client unique ID
   */
  public UniqueId getClientId()
  {
    return mClientId;
  }
  
  /**
   * Check if the handler is still running
   * 
   * @return {@code true} if handler is still running, {@code false} otherwise
   */
  public synchronized boolean isRunning()
  {
    return mIsRunning;
  }
  
  /**
   * Stop the handler
   */
  public void stop()
  {
    setRunningState(false);
  }

  /**
   * Set the handler's running state
   * 
   * @param isRunning A boolean indicating whether the handler should run ({@code true}) or stop ({@code false})
   */
  public synchronized void setRunningState(boolean isRunning)
  {
    mIsRunning = isRunning;
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
      .append(pad).append("  Client Id=").append(mClientId.toString()).append("\n")
      .append(pad).append("  Messenger=").append(mMessenger.toPrintableString(0)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
