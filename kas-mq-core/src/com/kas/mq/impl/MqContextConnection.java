package com.kas.mq.impl;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqConnection;
import com.kas.mq.internal.MqRequestFactory;

/**
 * A {@link MqContextConnection} is an extended {@link MqConnection} used by {@link MqContext}
 * 
 * @author Pippo
 */
public class MqContextConnection extends MqConnection
{
  /**
   * Constructing the connection
   */
  protected MqContextConnection()
  {
    super();
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
    mLogger.debug("MqContextConnection::defineQueue() - IN");
    
    IMqMessage request = MqRequestFactory.createDefineQueueRequest(queue, threshold);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::defineQueue() - OUT");
    return success;
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.debug("MqContextConnection::deleteQueue() - IN");
    
    IMqMessage request = MqRequestFactory.createDeleteQueueRequest(queue, force);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::deleteQueue() - OUT");
    return success;
  }

  /**
   * Query KAS/MQ server for information
   * 
   * @param qType a {@link EQueryType} value that describes the type of query
   * @param qProps a {@link Properties} object used as query parameters for refining the query
   * @return the message returned by the KAS/MQ server
   */
  public MqStringMessage queryServer(EQueryType qType, Properties qProps)
  {
    mLogger.debug("MqContextConnection::queryServer() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryServerRequest(qType, qProps);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
      {
        resp = reply.getResponse().getDesc();
      }
      else
      {
        resp = "Failed to receive reply for latest request: " + request.getRequestType().toString();
      }
      logInfoAndSetResponse(resp);
    }
    
    mLogger.debug("MqContextConnection::queryServer() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Terminate a connection
   * 
   * @param id The connection ID to terminate
   * @return the {@code true} if connection was terminated, {@code false} otherwise
   */
  public boolean termConn(UniqueId id)
  {
    mLogger.debug("MqContextConnection::termConn() - IN");
    
    IMqMessage request = MqRequestFactory.createTermConnRequest(id);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::termConn() - OUT");
    return success;
  }
  
  /**
   * Terminate a session
   * 
   * @param id The session ID to terminate
   * @return the {@code true} if session was terminated, {@code false} otherwise
   */
  public boolean termSess(UniqueId id)
  {
    mLogger.debug("MqContextConnection::termSess() - IN");
    
    IMqMessage request = MqRequestFactory.createTermSessRequest(id);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::termSess() - OUT");
    return success;
  }
  
  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqContextConnection::shutdown() - IN");
    
    IMqMessage request = MqRequestFactory.createShutdownRequest(mUser);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::shutdown() - OUT");
    return success;
  }
  
  /**
   * Send an administrative request to KAS/MQ server.
   * 
   * @param request The request message
   * @return The reply message, if there is one.
   */
  protected IMqMessage requestReply(IMqMessage request)
  {
    mLogger.debug("MqContextConnection::sendAdminRequest() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      reply = put(IMqConstants.cAdminQueueName, request);
    }
    
    mLogger.debug("MqContextConnection::sendAdminRequest() - OUT");
    return reply;
  }
  
  /**
   * Send an administrative request to KAS/MQ server and analyze response.<br>
   * <br>
   * The reply's description will be set to the latest response
   * 
   * @param request The request message
   * @return {@code true} if successful, {@code false} otherwise
   */
  protected boolean requestReplyAndAnalyze(IMqMessage request)
  {
    mLogger.debug("MqContextConnection::sendAdminRequest() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
      {
        success = reply.getResponse().getCode() == EMqCode.cOkay;
        resp = reply.getResponse().getDesc();
      }
      else
      {
        resp = "Failed to receive reply for latest request: " + request.getRequestType().toString();
      }
      logInfoAndSetResponse(resp);
    }
    
    mLogger.debug("MqContextConnection::sendAdminRequest() - OUT");
    return success;
  }
}
