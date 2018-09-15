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
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.ERequestType;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqRequestFactory;
import com.kas.mq.impl.internal.MqResponse;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.RemoteQueuesManager;

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
   * A responder's client
   */
  private ClientResponder mResponder;
  
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
  SessionHandler(Socket socket, IController controller) throws IOException
  {
    mController = controller;
    socket.setSoTimeout(mController.getConfig().getConnSocketTimeout());
    mMessenger = MessengerFactory.create(socket);
    
    mResponder = new ClientResponder(mController.getRepository());
    mSessionId = UniqueId.generate();
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mActiveUserName = null;
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
    IMqMessage<?> reply = null;
    try
    {
      IMqMessage<?> request = (IMqMessage<?>)packet;
      ERequestType requestType = request.getRequestType();
      mLogger.debug("SessionHandler::process() - Received request of type: " + requestType.toPrintableString(0));
      
      if (!mController.getConfig().isEnabled())
      {
        mLogger.debug("SessionHandler::process() - KAS/MQ server is disabled and request will be rejected");
        reply = reject(request);
      }
      else if (requestType == ERequestType.cLogin)
      {
        reply = propcessAuthenticate(request);
        if (reply.getResponse().getCode() != EMqCode.cOkay)
          cont = false;
      }
      else if (requestType == ERequestType.cDefineQueue)
      {
        reply = processDefineQueue(request);
      }
      else if (requestType == ERequestType.cDeleteQueue)
      {
        reply = processDeleteQueue(request);
      }
      else if (requestType == ERequestType.cQueryQueue)
      {
        reply = processQueryQueue(request);
      }
      else if (requestType == ERequestType.cSysState)
      {
        reply = processStateChange(request);
      }
      else if (requestType == ERequestType.cGet)
      {
        reply = processGet(request);
      }
      else if (requestType == ERequestType.cShutdown)
      {
        reply = processShutdown(request);
        if (reply.getResponse().getCode() != EMqCode.cOkay)
          cont = false;
      }
      else
      {
        processPut(request);
      }
      
      if (reply != null)
      {
        mLogger.debug("SessionHandler::process() - Responding with the message: " + StringUtils.asPrintableString(reply));
        mMessenger.send(reply);
      }
    }
    catch (ClassCastException e)
    {
      mLogger.error("Invalid message received from remote client. Message: " + packet);
      cont = false;
    }
    catch (IOException e)
    {
      mLogger.error("Failed to send response message to remote client. Message: " + StringUtils.asString(reply));
      cont = false;
    }
    
    mLogger.debug("SessionHandler::process() - OUT, Returns=" + cont);
    return cont;
  }
  
  /**
   * Process authenticate request.<br>
   * <br>
   * Read the user name and its password, then - if user name is a valid user - compare password
   * to the one defined in the configuration. If passwords match - authentication is successful. 
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response object
   */
  private IMqMessage<?> propcessAuthenticate(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::authenticate() - IN");
    
    String desc;
    EMqCode erc = EMqCode.cFail;
    
    String user = request.getStringProperty(IMqConstants.cKasPropertyUserName, null);
    String sb64pass = request.getStringProperty(IMqConstants.cKasPropertyPassword, null);
    byte [] confPwd = mController.getConfig().getUserPassword(user);
    String sb64ConfPass = StringUtils.asHexString(confPwd);
    
    if ((user == null) || (user.length() == 0))
      desc = "Invalid user name";
    else if (confPwd == null)
      desc = "User " + user + " is not defined";
    else if (!sb64pass.equals(sb64ConfPass))
      desc = "Incorrect password for " + user;
    else
    {
      desc = "User " + user + " successfully authenticated";
      mActiveUserName = user;
      erc = EMqCode.cOkay;
    }
    
    IMqMessage<?> result = MqMessageFactory.createResponse(request, erc, erc.ordinal(), desc);
    mLogger.debug("SessionHandler::authenticate() - OUT");
    return result;
  }
  
  /**
   * Process a define queue request.<br>
   * <br>
   * Extract the queue name and the threshold and pass them to the client responder.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response message
   */
  private IMqMessage<?> processDefineQueue(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::defineQueue() - IN");
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyDefqQueueName, null);
    int threshold = request.getIntProperty(IMqConstants.cKasPropertyDefqThreshold, IMqConstants.cDefaultQueueThreshold);
    
    boolean success = mResponder.defineQueue(queue, threshold);
    EMqCode erc = EMqCode.cOkay;
    if (!success)
      erc = EMqCode.cFail;
    
    IMqMessage<?> result = MqMessageFactory.createResponse(request, erc, erc.ordinal(), mResponder.getResponse());
    mLogger.debug("SessionHandler::defineQueue() - OUT");
    return result;
  }
  
  /**
   * Process delete queue request.<br>
   * <br>
   * Extract the queue name and the <b>force indicator</b> and pass them to the client responder.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response object
   */
  private IMqMessage<?> processDeleteQueue(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::deleteQueue() - IN");
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyDelqQueueName, null);
    boolean force = request.getBoolProperty(IMqConstants.cKasPropertyDelqForce, false);
    
    boolean success = mResponder.deleteQueue(queue, force);
    EMqCode erc = EMqCode.cOkay;
    if (!success)
      erc = EMqCode.cFail;
    
    IMqMessage<?> result = MqMessageFactory.createResponse(request, erc, erc.ordinal(), mResponder.getResponse());
    mLogger.debug("SessionHandler::deleteQueue() - OUT");
    return result;
  }
  
  /**
   * Process query queue request.<br>
   * <br>
   * Extract the queue name and the <b>prefix & alldata indicators</b> and pass them to the client responder.<br>
   * If this method was actually called for a {@code Synch} request, before returning the response,
   * the session handler tries to re-initialize the remote manager, in case it was not initialized properly.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response object
   */
  private IMqMessage<?> processQueryQueue(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::queryQueue() - IN");
    
    String origin = request.getStringProperty(IMqConstants.cKasPropertyQryqQmgrName, null);
    boolean all = request.getBoolProperty(IMqConstants.cKasPropertyQryqAllData, false);
    boolean prefix = request.getBoolProperty(IMqConstants.cKasPropertyQryqPrefix, false);
    String name = request.getStringProperty(IMqConstants.cKasPropertyQryqQueueName, "");
    
    Properties props = mResponder.queryQueue(name, prefix, all);
    
    int val = props.size();
    EMqCode rc = EMqCode.cOkay;
    if (val == 0) rc = EMqCode.cWarn;
    
    IMqMessage<?> result = MqMessageFactory.createResponse(request, rc, val, mResponder.getResponse());
    result.setSubset(props);
    
    // is this request originated from a remote qmgr?
    if (origin != null)
    {
      // since queryQmgr is able to query this manager's queues
      // it is just like this manager received a SysState request from queryQmgr
      MqManager manager = mController.getRepository().getRemoteManager(origin);
      if (!manager.isActive())
      {
        IMqMessage<?> sysRequest = MqRequestFactory.createSystemStateMessage(origin, true);
        processStateChange(sysRequest);
      }
    }
    
    mLogger.debug("SessionHandler::queryQueue() - OUT");
    return result;
  }
  
  /**
   * Process system state change request.<br>
   * <br>
   * Change the corresponding remote queues manager state from active to inactive or vice versa.
   * In addition, for {@code activate} request, the request will also hold the list of queues
   * defined in the queues manager that originated the request, so we need to update the corresponding
   * {@link RemoteQueuesManager} with its queue list.<br>
   * In addition to that, the response message (for {@code activate} requests, will include the list
   * of queues defined in the local queue manager.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response object
   */
  private IMqMessage<?> processStateChange(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::stateChange() - IN");
    
    String sender = request.getStringProperty(IMqConstants.cKasPropertySyssQmgrName, null);
    boolean activated = request.getBoolProperty(IMqConstants.cKasPropertySyssActive, false);
    
    mLogger.debug("SessionHandler::stateChange() - Manager at " + sender + " changed its state to " + (activated ? "active" : "inactive"));
    
    IMqMessage<?> result = null;
    
    IRepository repo = mController.getRepository();
    MqManager manager = repo.getRemoteManager(sender);
    if (!activated && manager.isActive())
    {
      manager.deactivate();
    }
    else if (activated && !manager.isActive())
    {
      manager.activate();
      Properties remoteQueues = request.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
      ((RemoteQueuesManager)manager).setQueues(remoteQueues);
      
      Properties localQueues = repo.queryLocalQueues("", true, false);
      result = MqMessageFactory.createResponse(request, EMqCode.cOkay, 0, "");
      result.setSubset(localQueues);
    }
    
    mLogger.debug("SessionHandler::stateChange() - OUT");
    return result;
  }
  
  /**
   * Process get message from specified queue.<br>
   * <br>
   * Extract the timeout, interval and queue name and pass them client responder.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} retrieved from the queue or a response object
   */
  private IMqMessage<?> processGet(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::get() - IN");
    
    long timeout  = request.getLongProperty(IMqConstants.cKasPropertyGetTimeout, IMqConstants.cDefaultTimeout);
    long interval = request.getLongProperty(IMqConstants.cKasPropertyGetInterval, IMqConstants.cDefaultPollingInterval);
    String qname  = request.getStringProperty(IMqConstants.cKasPropertyGetQueueName, null);
    
    IMqMessage<?> msg = mResponder.get(qname, timeout, interval);
    
    MqLocalQueue mqq = mController.getRepository().getLocalQueue(qname);
    if (mqq != null)
    {
      String user = request.getStringProperty(IMqConstants.cKasPropertyGetUserName, null);
      mqq.setLastAccess(user, "get");
    }
    
    if (msg == null)
      msg = MqMessageFactory.createResponse(request, EMqCode.cFail, EMqCode.cFail.ordinal(), mResponder.getResponse());
    else
      msg.setResponse(new MqResponse(EMqCode.cOkay, 0, ""));
    
    mLogger.debug("SessionHandler::get() - OUT");
    return msg;
  }
  
  /**
   * Process put message to specified queue.<br>
   * <br>
   * Extract the queue name and pass it along with the request message to the client responder.
   * 
   * @param request The request message
   */
  private void processPut(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::put() - IN");
    
    String qname = request.getStringProperty(IMqConstants.cKasPropertyPutQueueName, null);
    
    mResponder.put(qname, request);
    
    mLogger.debug("SessionHandler::put() - OUT");
  }
  
  /**
   * Process shutdown request.<br>
   * <br>
   * Signal the controller to shutdown if and only if the current user is {@code admin}.
   * 
   * @param request The request message
   * @return The {@link IMqMessage} response object
   */
  private IMqMessage<?> processShutdown(IMqMessage<?> request)
  {
    mLogger.debug("SessionHandler::shutdown() - IN");
    
    EMqCode erc = EMqCode.cFail;
    String desc = "Cannot shutdown KAS/MQ server with non-admin user";
    String user = mActiveUserName;
    if ("admin".equalsIgnoreCase(user))
    {
      erc = EMqCode.cOkay;
      desc = "Shutdown request was successfully posted";
      mController.shutdown();
    }
    
    IMqMessage<?> result = MqMessageFactory.createResponse(request, erc, erc.ordinal(), desc);
    mLogger.debug("SessionHandler::shutdown() - OUT");
    return result;
  }
  
  /**
   * Generate a {@link IMqMessage} which will be sent back to remote client, rejecting the request
   * 
   * @param request The request {@link IMqMessage}
   * @return the {@link IMqMessage} response object
   */
  private IMqMessage<?> reject(IMqMessage<?> request)
  {
    return MqMessageFactory.createResponse(request, EMqCode.cError, EMqCode.cError.ordinal(), "KAS/MQ server is disabled, operation rejected");
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
      .append(pad).append("  Session Id=").append(mSessionId.toString()).append("\n")
      .append(pad).append("  Messenger=").append(mMessenger.toPrintableString(0)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
