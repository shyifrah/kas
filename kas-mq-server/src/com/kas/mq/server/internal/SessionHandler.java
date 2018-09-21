package com.kas.mq.server.internal;

import java.io.EOFException;
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
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.ERequestType;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.processors.IProcessor;
import com.kas.mq.server.processors.ProcessorFactory;

/**
 * A {@link SessionHandler} is the object that handles the traffic in and from a remote client.
 * 
 * @author Pippo
 */
public class SessionHandler extends AKasObject implements Runnable
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * The sessions controller
   */
  private IController mController;
  
  /**
   * The server's repository
   */
  private IRepository mRepository;
  
  /**
   * Messenger
   */
  private IMessenger mMessenger;
  
  /**
   * The session ID for this session
   */
  private UniqueId mSessionId;
  
  /**
   * Active user name
   */
  private String mActiveUserName;
  
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
  SessionHandler(Socket socket, IController controller, IRepository repository) throws IOException
  {
    mController = controller;
    mRepository = repository;
    socket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mMessenger = MessengerFactory.create(socket);
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    mSessionId = UniqueId.generate();
    mActiveUserName = null;
  }
  
  /**
   * Running the handler - keep reading and process packets from input stream.<br>
   * If necessary, respond on output stream.
   */
  public void run()
  {
    mLogger.debug("SessionHandler::run() - IN");
    mController.onHandlerStart(this);
    
    while (isRunning())
    {
      mLogger.debug("SessionHandler::run() - Waiting for messages from client...");
      try
      {
        IPacket packet = mMessenger.receive();
        mLogger.debug("SessionHandler::run() - Received packet: " + StringUtils.asPrintableString(packet));
        if (packet != null)
        {
          IMqMessage<?> request = (IMqMessage<?>)packet;
          ERequestType requestType = request.getRequestType();
          mLogger.debug("SessionHandler::run() - Received request of type: " + StringUtils.asPrintableString(requestType));
          
          boolean success = false;
          
          IProcessor processor = ProcessorFactory.newProcessor(mController, mRepository, this, request);
          IMqMessage<?> reply = processor.process();
          
          // not all processors generates a reply, but if there is one - send it back
          if (reply != null)
          {
            mLogger.debug("SessionHandler::run() - Responding with the message: " + StringUtils.asPrintableString(reply));
            mMessenger.send(reply);
          }
          
          success = processor.postprocess(reply);
          setRunningState(success);
        }
      }
      catch (SocketTimeoutException e)
      {
        mLogger.diag("SessionHandler::run() - Socket Timeout occurred. Resume waiting for a new packet from client...");
      }
      catch (SocketException | EOFException e)
      {
        mLogger.info("Connection to remote host at " + mMessenger.getAddress() + " was lost");
        stop();
      }
      catch (IOException e)
      {
        mLogger.error("An I/O error occurred while trying to receive packet from remote client. Exception: ", e);
        stop();
      }
      catch (Throwable e)
      {
        mLogger.error("An unknown exception caught while processing client requests. Exception: ", e);
        stop();
      }
    }
    
    mController.onHandlerEnd(this);
    mLogger.debug("SessionHandler::run() - OUT");
  }
  
  /**
   * Set the active user name to {@code user}
   * 
   * @param user The new active user name
   */
  public void setActiveUserName(String user)
  {
    mActiveUserName = user;
  }
  
  /**
   * Get the active user name
   * 
   * @return the active user name
   */
  public String getActiveUserName()
  {
    return mActiveUserName;
  }
  
  /**
   * Set the session's unique ID
   * 
   * @param sessId The session's unique ID to set
   */
  public void setSessionId(UniqueId sessId)
  {
    mSessionId = sessId;
  }
  
  /**
   * Get the session's unique ID
   * 
   * @return the session's unique ID
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
   * Signal the handler to stop after fulfilling last request
   */
  public void stop()
  {
    setRunningState(false);
  }

  /**
   * Forcefully killing the session.<br>
   * This method is called by the {@link IController} in circumstances where the session seems hung 
   */
  void killSession()
  {
    mMessenger.cleanup();
    mMessenger = null;
  }

  /**
   * Set the handler's running state
   * 
   * @param isRunning A boolean indicating whether the handler should run ({@code true}) or stop ({@code false})
   */
  private synchronized void setRunningState(boolean isRunning)
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
      .append(pad).append("  IsRunning=").append(mIsRunning).append("\n")
      .append(pad).append("  SessionId=").append(mSessionId.toString()).append("\n")
      .append(pad).append("  ActiveUser=").append(mActiveUserName).append("\n")
      .append(pad).append("  Messenger=").append(mMessenger.toPrintableString(0)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
