package com.kas.mq.impl.internal;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import com.kas.comm.IMessenger;
import com.kas.comm.impl.MessengerFactory;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;

/**
 * A client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 */
public class MqClientImpl extends AKasObject
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
   * The session ID
   */
  private UniqueId mSessionId = null;
  
  /**
   * Active user.<br>
   * This data member is set after each successful {@link #login(String, String) login}.
   */
  private String mUser = IMqConstants.cSystemUserName;
  
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
   * @param host The host name or IP address (uppercased)
   * @param port The port number
   */
  public void connect(String host, int port)
  {
    mLogger.debug("MqClientImpl::connect() - IN");
    
    if (isConnected()) disconnect();
    
    NetworkAddress addr = new NetworkAddress(host, port);
    
    try
    {
      mMessenger = MessengerFactory.create(host, port);
    }
    catch (ConnectException e)
    {
      logErrorAndSetResponse("Connection to [" + addr + "] refused");
    }
    catch (IOException e)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("Exception occurred while trying to connect to [")
        .append(addr).append("]. Exception: ").append(StringUtils.format(e));
      logErrorAndSetResponse(sb.toString());
    }
    
    mLogger.debug("MqClientImpl::connect() - OUT");
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
      mUser = IMqConstants.cSystemUserName;
      mSessionId = null;
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
   * login to KAS/MQ server.
   * 
   * @param user The user's name
   * @param pwd The user's password
   * @return {@code true} if {@code password} matches the user's password as defined in {@link MqConfiguration}, {@code false} otherwise
   */
  public boolean login(String user, String pwd)
  {
    mLogger.debug("MqClientImpl::login() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      UniqueId uid = mSessionId == null ? UniqueId.generate() : mSessionId;
      IMqMessage<?> request = MqRequestFactory.createLoginRequest(user, pwd, uid);
      mLogger.debug("MqClientImpl::login() - sending login request: " + request.toPrintableString(0));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::login() - received response: " + reply.toPrintableString(0));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
        {
          success = true;
          mUser = user;
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
      }
    }
    
    mLogger.debug("MqClientImpl::login() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public boolean defineQueue(String queue, int threshold)
  {
    mLogger.debug("MqClientImpl::defineQueue() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createDefineQueueRequest(queue, threshold);
      mLogger.debug("MqClientImpl::defineQueue() - sending define request: " + request.toPrintableString(0));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::defineQueue() - received response: " + reply.toPrintableString(0));
        if ((reply.getResponse().getCode() == EMqCode.cOkay) || (reply.getResponse().getCode() == EMqCode.cWarn))
          success = true;
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to define queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::defineQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.debug("MqClientImpl::deleteQueue() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createDeleteQueueRequest(queue, force);
      mLogger.debug("MqClientImpl::deleteQueue() - sending delete request: " + request.toPrintableString(0));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::deleteQueue() - received response: " + reply.toPrintableString(0));
        if ((reply.getResponse().getCode() == EMqCode.cOkay) || (reply.getResponse().getCode() == EMqCode.cWarn))
          success = true;
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to delete queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::deleteQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all if {@code true}, display all information on all queues 
   * @return the queues that returned that matched the query
   */
  public Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("MqClientImpl::queryQueue() - IN");
    
    Properties result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createQueryQueueRequest(name, prefix, all);
      mLogger.debug("MqClientImpl::queryQueue() - sending query queue request: " + request.toPrintableString(0));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::queryQueue() - received response: " + reply.toPrintableString(0));
        result = reply.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
        
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to query KAS/MQ server for queues prefixed with ")
          .append(name).append(". Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::queryQueue() - OUT, TotalProperties=" + (result == null ? 0 : result.size()));
    return result;
  }

  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   */
  public IMqMessage<?> get(String queue, long timeout, long interval)
  {
    mLogger.debug("MqClientImpl::get() - IN");
    
    IMqMessage<?> result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      try
      {
        IMqMessage<?> request = MqRequestFactory.createGetRequest(queue, timeout, interval);
        mLogger.debug("MqClientImpl::get() - sending get request: " + StringUtils.asPrintableString(request));
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::get() - received response: " + StringUtils.asPrintableString(reply));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
        {
          reply.setStringProperty(IMqConstants.cKasPropertyGetUserName, mUser);
          reply.setStringProperty(IMqConstants.cKasPropertyGetTimeStamp, TimeStamp.nowAsString());
          result = reply;
          setResponse("Successfully got a message from queue " + queue + ", Message: " + StringUtils.asPrintableString(reply));
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
    
    mLogger.debug("MqClientImpl::get() - OUT");
    return result;
  }
  
  /**
   * Put a message into the specified queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   */
  public void put(String queue, IMqMessage<?> message)
  {
    mLogger.debug("MqClientImpl::put() - IN");
    
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      try
      {
        message.setStringProperty(IMqConstants.cKasPropertyPutQueueName, queue);
        message.setStringProperty(IMqConstants.cKasPropertyPutUserName, mUser);
        message.setStringProperty(IMqConstants.cKasPropertyPutTimeStamp, TimeStamp.nowAsString());
        mMessenger.send(message);
        logDebugAndSetResponse("put", "Message was successfully sent to server to put into queue " + queue);
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to put a message into queue [")
          .append(queue).append("]. Exception: ").append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::put() - OUT");
  }
  
  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqClientImpl::shutdown() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createShutdownRequest(mUser);
      mLogger.debug("MqClientImpl::shutdown() - sending shutdown request: " + request.toPrintableString(0));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::shutdown() - received response: " + reply.toPrintableString(0));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
          success = true;
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to signal KAS/MQ server to shutdown. Exception: ")
          .append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::shutdown() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Notify KAS/MQ server that the sender wishes to update it state
   * 
   * @param request The system-state change request. The message contains the new state
   * of the sender KAS/MQ server.
   * @param waitResponse When {@code true}, caller expects a response message, {@code false} otherwise
   * @return the reply message from the receiver
   */
  public IMqMessage<?> notifySysState(IMqMessage<?> request, boolean waitResponse)
  {
    mLogger.debug("MqClientImpl::notifySysState() - IN");
    
    IMqMessage<?> reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      mLogger.debug("MqClientImpl::notifySysState() - sending notify request: " + StringUtils.asPrintableString(request));
      try
      {
        String desc = "";
        if (!waitResponse)
        {
          mMessenger.send(request);
        } 
        else
        {
          reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
          mLogger.debug("MqClientImpl::notifySysState() - received response: " + StringUtils.asPrintableString(reply));
          desc = reply.getResponse().getDesc();
        }
        logInfoAndSetResponse(desc);
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to notify KAS/MQ server about its system state. Exception: ")
          .append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::notifySysState() - OUT");
    return reply;
  }
  
  /**
   * Notify remote KAS/MQ server that the sender updated its repository
   * 
   * @param qmgr The name of the KAS/MQ server whose repository was updated
   * @param queue The name of the queue that was subject to update
   * @param added If {@code true}, the queue was added, else it was removed
   * @return {@code true} if remote KAS/MQ server was successfully notified, otherwise {@code false}
   */
  public boolean notifyRepoUpdate(String qmgr, String queue, boolean added)
  {
    mLogger.debug("MqClientImpl::notifyRepoUpdate() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createRepositoryUpdateMessage(qmgr, queue, added);
      mLogger.debug("MqClientImpl::notifyRepoUpdate() - sending notify request: " + StringUtils.asPrintableString(request));
      try
      {
        IMqMessage<?> reply = (IMqMessage<?>)mMessenger.sendAndReceive(request);
        mLogger.debug("MqClientImpl::notifyRepoUpdate() - received response: " + reply.toPrintableString(0));
        if (reply.getResponse().getCode() == EMqCode.cOkay)
          success = true;
        logInfoAndSetResponse(reply.getResponse().getDesc());
      }
      catch (IOException e)
      {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occurred while trying to signal KAS/MQ server that repository was updated. Exception: ")
          .append(StringUtils.format(e));
        logErrorAndSetResponse(sb.toString());
      }
    }
    
    mLogger.debug("MqClientImpl::notifyRepoUpdate() - OUT");
    return success;
  }
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   */
  public String getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   */
  public void setResponse(String response)
  {
    mResponse = response;
  }
  
  /**
   * Log DEBUG a message
   * 
   * @param message The message to log and set as the client's response
   */
  private void logDebugAndSetResponse(String method, String message)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName())
      .append("::")
      .append(method)
      .append("() - ")
      .append(message);
    
    mLogger.debug(sb.toString());
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
   * Get string representation of the object
   * 
   * @return string representation of the object
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Session...........: ").append(StringUtils.asString(mSessionId)).append('\n');
    sb.append("  User.......: ").append(StringUtils.asString(mUser)).append('\n');
    sb.append("  Connection.: ").append(StringUtils.asString(mMessenger)).append('\n');
    return sb.toString();
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
