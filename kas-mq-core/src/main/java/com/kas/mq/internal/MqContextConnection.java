package com.kas.mq.internal;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.StringList;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;

/**
 * A {@link MqContextConnection} is an extended {@link MqConnection} used by {@link MqContext}
 * 
 * @author Pippo
 */
public class MqContextConnection extends MqConnection
{
  /**
   * Constructing the connection
   * 
   * @param clientName A name used by the client application
   */
  public MqContextConnection(String clientName)
  {
    super(clientName);
  }

  /**
   * Define a new group.
   * 
   * @param group The name of the group to define
   * @param desc The group description
   * @return the {@code true} if group was defined, {@code false} otherwise
   */
  public boolean defineGroup(String group, String desc)
  {
    mLogger.debug("MqContextConnection::defineGroup() - IN");
    
    String name = group.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineGroupRequest(name, desc);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::defineGroup() - OUT");
    return success;
  }
  
  /**
   * Delete an existing group.
   * 
   * @param group The name of the group to delete
   * @return the {@code true} if group was deleted, {@code false} otherwise
   */
  public boolean deleteGroup(String group)
  {
    mLogger.debug("MqContextConnection::deleteGroup() - IN");
    
    String name = group.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteGroupRequest(name);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::deleteGroup() - OUT");
    return success;
  }
  
  /**
   * Define a new user
   * 
   * @param user The name of the user to define
   * @param pass The user's password
   * @param desc The user description
   * @param groups The list of groups the user is to be a member of
   * @return the {@code true} if user was defined, {@code false} otherwise
   */
  public boolean defineUser(String user, String pass, String desc, StringList groups)
  {
    mLogger.debug("MqContextConnection::defineUser() - IN");
    
    String name = user.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineUserRequest(name, pass, desc, groups);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::defineUser() - OUT");
    return success;
  }
  
  /**
   * Delete an existing user
   * 
   * @param user The name of the user to delete
   * @return the {@code true} if user was deleted, {@code false} otherwise
   */
  public boolean deleteUser(String user)
  {
    mLogger.debug("MqContextConnection::deleteUser() - IN");
    
    String name = user.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteUserRequest(name);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::deleteUser() - OUT");
    return success;
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The name of the queue to define
   * @param desc The queue description
   * @param threshold The queue threshold
   * @param disp The queue disposition
   * @return the {@code true} if queue was defined, {@code false} otherwise
   */
  public boolean defineQueue(String queue, String desc, int threshold, EQueueDisp disp)
  {
    mLogger.debug("MqContextConnection::defineQueue() - IN");
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineQueueRequest(qname, desc, threshold, disp);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::defineQueue() - OUT");
    return success;
  }
  
  /**
   * Alter a new queue.
   * 
   * @param queue The queue name to be altered.
   * @param qProps The queue properties to be altered
   * @return the {@code true} if queue was altered, {@code false} otherwise
   */
  public boolean alterQueue(String queue, Properties qProps)
  {
    mLogger.debug("MqContextConnection::alterQueue() - IN");
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createAlterQueueRequest(qname, qProps);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::alterQueue() - OUT");
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
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteQueueRequest(qname, force);
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
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
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
  public boolean termServer()
  {
    mLogger.debug("MqContextConnection::termServer() - IN");
    
    IMqMessage request = MqRequestFactory.createTermServerRequest(mUser);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqContextConnection::termServer() - OUT");
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
    mLogger.debug("MqContextConnection::requestReply() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      reply = put(IMqConstants.cAdminQueueName, request);
    }
    
    mLogger.debug("MqContextConnection::requestReply() - OUT");
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
    mLogger.debug("MqContextConnection::requestReplyAndAnalyze() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      String resp = "Connection to remote host was lost"; // if we get null reply
      if (reply != null)
      {
        success = reply.getResponse().getCode() == EMqCode.cOkay;
        resp = reply.getResponse().getDesc();
      }
      logInfoAndSetResponse(resp);
    }
    
    mLogger.debug("MqContextConnection::requestReplyAndAnalyze() - OUT");
    return success;
  }
}

