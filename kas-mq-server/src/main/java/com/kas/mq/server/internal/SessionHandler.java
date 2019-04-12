package com.kas.mq.server.internal;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.IMessenger;
import com.kas.comm.IPacket;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.processors.IProcessor;
import com.kas.mq.server.processors.ProcessorFactory;
import com.kas.sec.entities.UserEntity;

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
  private Logger mLogger;
  
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
   * Active user
   */
  private UserEntity mActiveUser;
  
  /**
   * Indicator whether handler is still running
   */
  private boolean mIsRunning = true;
  
  /**
   * Construct a {@link SessionHandler} to handle all incoming and outgoing traffic from a remote client.<br>
   * Client's transmits messages and received by this handler over the specified {@code socket}.
   *  
   * @param socket
   *   The client's socket
   * @param controller
   *   The {@link IController}
   * @param repository
   *   The {@link IRepository}
   * @throws IOException
   *   if {@link Socket#setSoTimeout()} throws
   */
  SessionHandler(Socket socket, IController controller, IRepository repository) throws IOException
  {
    mController = controller;
    mRepository = repository;
    mMessenger = MessengerFactory.create(socket, mController.getConfig().getConnSocketTimeout());
    
    mLogger = LogManager.getLogger(getClass());
    mSessionId = UniqueId.generate();
    mActiveUser = null;
  }
  
  /**
   * Running the handler - keep reading and process packets from input stream.<br>
   * If necessary, respond on output stream.
   */
  public void run()
  {
    mLogger.trace("SessionHandler::run() - IN");
    mController.onHandlerStart(this);
    
    NetworkAddress remoteAddress = mMessenger.getAddress();
    
    while (isRunning())
    {
      mLogger.trace("SessionHandler::run() - Waiting for messages from client...");
      try
      {
        IPacket packet = mMessenger.receive();
        mLogger.trace("SessionHandler::run() - Received packet: {}", StringUtils.asPrintableString(packet));
        if (packet != null)
        {
          IMqMessage request = (IMqMessage)packet;
          ERequestType requestType = request.getRequestType();
          mLogger.trace("SessionHandler::run() - Received request of type: {}", StringUtils.asPrintableString(requestType));
          
          IProcessor processor = ProcessorFactory.newProcessor(request, this, mRepository);
          IMqMessage reply = processor.process();
          
          mLogger.trace("SessionHandler::run() - Responding with the message: {}", StringUtils.asPrintableString(reply));
          mMessenger.send(reply);
          
          boolean running = isRunning() & processor.postprocess(reply);
          setRunningState(running);
        }
      }
      catch (SocketTimeoutException e)
      {
        mLogger.trace("SessionHandler::run() - Socket Timeout occurred. Resume waiting for a new packet from client...");
      }
      catch (SocketException | EOFException e)
      {
        mLogger.info("Connection to remote host at {} was lost", remoteAddress);
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
    mLogger.trace("SessionHandler::run() - OUT");
  }
  
  /**
   * Get the sessions controller
   * 
   * @return
   *   the sessions controller
   */
  public IController getController()
  {
    return mController;
  }

  /**
   * Set the active user to {@code userEntity}
   * 
   * @param user
   *   The new active user name
   */
  public void setActiveUser(UserEntity userEntity)
  {
    mActiveUser = userEntity;
  }
  
  /**
   * Get the active user
   * 
   * @return
   *   the active user
   */
  public UserEntity getActiveUser()
  {
    return mActiveUser;
  }
  
  /**
   * Set the session's unique ID
   * 
   * @param sessId
   *   The session's unique ID to set
   */
  public void setSessionId(UniqueId sessId)
  {
    mSessionId = sessId;
  }
  
  /**
   * Get the session's unique ID
   * 
   * @return
   *   the session's unique ID
   */
  public UniqueId getSessionId()
  {
    return mSessionId;
  }
  
  /**
   * Check if the handler is still running
   * 
   * @return
   *   {@code true} if handler is still running, {@code false} otherwise
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
   * Set the handler's running state
   * 
   * @param isRunning
   *   a boolean indicating whether the handler should run ({@code true}) or stop ({@code false})
   */
  private synchronized void setRunningState(boolean isRunning)
  {
    mIsRunning = isRunning;
  }
  
  /**
   * Terminate the handler forcefully
   */
  public void end()
  {
    mMessenger.cleanup();
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
      .append(pad).append("  IsRunning=").append(mIsRunning).append("\n")
      .append(pad).append("  SessionId=").append(mSessionId.toString()).append("\n")
      .append(pad).append("  ActiveUser=").append(mActiveUser).append("\n")
      .append(pad).append("  Messenger=").append(mMessenger.toPrintableString(level+1)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
