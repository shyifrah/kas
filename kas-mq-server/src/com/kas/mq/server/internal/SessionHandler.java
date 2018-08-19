package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponseMessage;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.server.IController;
import com.kas.mq.server.IHandler;
import com.kas.mq.server.resp.SessionResponder;

/**
 * A {@link SessionHandler} is the object that handles the traffic in and from a remote client.
 * 
 * @author Pippo
 */
public class SessionHandler extends AKasObject implements Runnable, IHandler
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Messenger
   */
  private IMessenger mMessenger;
  
  /**
   * The session's unique ID
   */
  private UniqueId mSessionId;
  
  /**
   * The sessions controller
   */
  private IController mController;
  
  /**
   * The session responder
   */
  private SessionResponder mSessionResponder;
  
  /**
   * Active user name
   */
  private String mActiveUserName;
  
  /**
   * Active queue
   */
  private MqQueue mActiveQueue;
  
  /**
   * Indicator whether handler is still running
   */
  private boolean mIsRunning = true;
  
  /**
   * Construct a {@link SessionHandler} to handle all incoming and outgoing traffic from a remote client.<br>
   * <br>
   * Client's transmits messages and received by this handler over the specified {@code socket}.
   *  
   * @param socket The client's socket
   * 
   * @throws IOException if {@link Socket#setSoTimeout()} throws
   */
  SessionHandler(Socket socket, IController controller) throws IOException
  {
    mController = controller;
    socket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mMessenger = MessengerFactory.create(socket);
    
    mSessionId = UniqueId.generate();
    mLogger = LoggerFactory.getLogger(this.getClass());
    mSessionResponder = new SessionResponder(this);
  }
  
  /**
   * Running the handler - keep reading and process packets from input stream.<br>
   * If necessary, respond on output stream.
   */
  public void run()
  {
    mLogger.debug("SessionHandler::run() - IN");
    
    while (isRunning())
    {
      mLogger.debug("SessionHandler::run() - Waiting for messages from client...");
      try
      {
        IPacket packet = mMessenger.receive();
        if (packet != null)
        {
          mLogger.debug("SessionHandler::run() - Packet received: " + packet.toPrintableString(0));
          boolean success = process(packet);
          setRunningState(success);
        }
      }
      catch (SocketTimeoutException e)
      {
        mLogger.diag("SessionHandler::run() - Socket Timeout occurred. Resume waiting for a new packet from client...");
      }
      catch (SocketException e)
      {
        mLogger.info("Connection to remote host at " + mMessenger.getAddress() + " was dropped");
        stop();
      }
      catch (IOException e)
      {
        mLogger.error("An I/O error occurred while trying to receive packet from remote client. Exception: ", e);
        stop();
      }
    }
    
    mLogger.debug("SessionHandler::run() - OUT");
  }
  
  /**
   * Process received packet
   * 
   * @param packet The received packet
   * @return {@code true} if {@link SessionHandler} should continue processing next packet, {@code false} otherwise
   */
  private boolean process(IPacket packet)
  {
    mLogger.debug("SessionHandler::process() - IN");
    
    boolean cont = true;
    MqResponseMessage response = null;
    try
    {
      MqMessage request = (MqMessage)packet;
      ERequestType requestType = request.getRequestType();
      if (requestType == ERequestType.cAuthenticate)
      {
        response = mSessionResponder.authenticate(request);
        if (response.getResponseCode() != 0)
          cont = false;
      }
      else if (requestType == ERequestType.cOpenQueue)
      {
        response = mSessionResponder.open(request);
      }
      else if (requestType == ERequestType.cCloseQueue)
      {
        response = mSessionResponder.close(request);
      }
      
      mLogger.debug("SessionHandler::process() - Responding with the message: " + response.toPrintableString());
      mMessenger.send(response);
    }
    catch (ClassCastException e)
    {
      mLogger.error("Invalid message received from remote client. Message: " + packet);
      cont = false;
    }
    catch (IOException e)
    {
      mLogger.error("Failed to send response message to remote client. Message: " + StringUtils.asString(response));
      cont = false;
    }
    
    mLogger.debug("SessionHandler::process() - OUT, Returns=" + cont);
    return cont;
  }
  
  /**
   * Get the active user name
   * 
   * @return the active user name
   * 
   * @see com.kas.mq.server.IHandler#getActiveUserName()
   */
  public String getActiveUserName()
  {
    return mActiveUserName;
  }
  
  /**
   * Set the active user name
   * 
   * @param username The active user name
   * 
   * @see com.kas.mq.server.IHandler#setActiveUserName(String)
   */
  public void setActiveUserName(String username)
  {
    mActiveUserName = username;
  }
  
  /**
   * Get the active queue
   * 
   * @return the active queue, or {@code null} if closed
   * 
   * @see com.kas.mq.server.IHandler#getQueue()
   */
  public MqQueue getActiveQueue()
  {
    return mActiveQueue;
  }
  
  /**
   * Set the active queue
   * 
   * @param queue The active queue
   * 
   * @see com.kas.mq.server.IHandler#setQueue(MqQueue)
   */
  public void setActiveQueue(MqQueue queue)
  {
    mActiveQueue = queue;
  }
  
  /**
   * Get indication if a user's password matches the one defined in the {@link MqConfiguration}
   * 
   * @param user The user's name
   * @param pass The user's password
   * @return {@code true} if the user's password matches the one defined in the {@link MqConfiguration}, {@code false} otherwise
   * 
   * @see com.kas.mq.server.IHandler#isPasswordMatch(String, String)
   */
  public boolean isPasswordMatch(String user, String pass)
  {
    String confPass = mController.getConfig().getUserPassword(user);
    if (confPass == null)
    {
      if (user == null)
        return true;
      else
        return false;
    }
    
    return confPass.equals(pass);
  }
  
  /**
   * Get queue by name
   * 
   * @param queue The queue name
   * @return the {@link MqQueue} object associated with the specified queue name
   * 
   * @see com.kas.mq.server.IHandler#getQueue(String)
   */
  public MqQueue getQueue(String name)
  {
    return mController.getRepository().getQueue(name);
  }
  
  /**
   * Get the session unique ID
   * 
   * @return the session unique ID
   */
  public UniqueId getSessionId()
  {
    return mSessionId;
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
      .append(pad).append("  Session Id=").append(mSessionId.toString()).append("\n")
      .append(pad).append("  Messenger=").append(mMessenger.toPrintableString(0)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
