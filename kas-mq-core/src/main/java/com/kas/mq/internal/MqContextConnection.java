package com.kas.mq.internal;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.StringList;
import com.kas.mq.impl.EQueryConfigType;
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
   * @param clientName
   *   A name used by the client application
   */
  public MqContextConnection(String clientName)
  {
    super(clientName);
  }

  /**
   * Define a new group.
   * 
   * @param group
   *   The name of the group to define
   * @param desc
   *   The group description
   * @return
   *   {@code true} if group was defined, {@code false} otherwise
   */
  public boolean defineGroup(String group, String desc)
  {
    mLogger.trace("MqContextConnection::defineGroup() - IN");
    
    String name = group.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineGroupRequest(name, desc);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::defineGroup() - OUT");
    return success;
  }
  
  /**
   * Define a new user
   * 
   * @param user
   *   The name of the user to define
   * @param pass
   *   The user's password
   * @param desc
   *   The user description
   * @param groups
   *   The list of groups the user is to be a member of
   * @return
   *   {@code true} if user was defined, {@code false} otherwise
   */
  public boolean defineUser(String user, String pass, String desc, StringList groups)
  {
    mLogger.trace("MqContextConnection::defineUser() - IN");
    
    String name = user.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineUserRequest(name, pass, desc, groups);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::defineUser() - OUT");
    return success;
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue
   *   The name of the queue to define
   * @param desc
   *   The queue description
   * @param threshold
   *   The queue threshold
   * @param disp
   *   The queue disposition
   * @return
   *   {@code true} if queue was defined, {@code false} otherwise
   */
  public boolean defineQueue(String queue, String desc, int threshold, EQueueDisp disp)
  {
    mLogger.trace("MqContextConnection::defineQueue() - IN");
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createDefineQueueRequest(qname, desc, threshold, disp);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::defineQueue() - OUT");
    return success;
  }
  
  /**
   * Delete an existing group.
   * 
   * @param group
   *   The name of the group to delete
   * @return
   *   {@code true} if group was deleted, {@code false} otherwise
   */
  public boolean deleteGroup(String group)
  {
    mLogger.trace("MqContextConnection::deleteGroup() - IN");
    
    String name = group.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteGroupRequest(name);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::deleteGroup() - OUT");
    return success;
  }
  
  /**
   * Delete an existing user
   * 
   * @param user
   *   The name of the user to delete
   * @return
   *   {@code true} if user was deleted, {@code false} otherwise
   */
  public boolean deleteUser(String user)
  {
    mLogger.trace("MqContextConnection::deleteUser() - IN");
    
    String name = user.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteUserRequest(name);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::deleteUser() - OUT");
    return success;
  }
  
  /**
   * Delete a new queue.
   * 
   * @param queue
   *   The queue name to delete.
   * @param force
   *   Should the queue be deleted even if its not empty.
   * @return
   *   {@code true} if queue was deleted, {@code false} otherwise
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.trace("MqContextConnection::deleteQueue() - IN");
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createDeleteQueueRequest(qname, force);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::deleteQueue() - OUT");
    return success;
  }

  /**
   * Query KAS/MQ server for information on group
   * 
   * @param group
   *   The name of the group to query
   * @param prefix
   *   If {@code true}, then {@code group} identifies a prefix of group names,
   *   otherwise, it is a group name.
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage queryGroup(String group, boolean prefix)
  {
    mLogger.trace("MqContextConnection::queryGroup() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryGroupRequest(group, prefix);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::queryGroup() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Query KAS/MQ server for information on user
   * 
   * @param user
   *   The name of the user to query
   * @param prefix
   *   If {@code true}, then {@code user} identifies a prefix of user names,
   *   otherwise, it is a user name.
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage queryUser(String group, boolean prefix)
  {
    mLogger.trace("MqContextConnection::queryUser() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryUserRequest(group, prefix);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::queryUser() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Query KAS/MQ server for information on queue
   * 
   * @param queue
   *   The name of the queue to query
   * @param prefix
   *   If {@code true}, then {@code queue} identifies a prefix of queue names,
   *   otherwise, it is a queue name.
   * @param allData
   *   If {@code true}, then all data is returned regarding the queue,
   *   otherwise, the queue name.
   * @param format
   *   If {@code true}, then output is formatted, otherwise it is returned as is.
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage queryQueue(String queue, boolean prefix, boolean allData, boolean format)
  {
    mLogger.trace("MqContextConnection::queryQueue() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryQueueRequest(queue, prefix, allData, format);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::queryQueue() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Query KAS/MQ server for information on connections
   * 
   * @param id
   *   The connection ID or {@code "ALL"}
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage queryConn(String id)
  {
    mLogger.trace("MqContextConnection::queryConn() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryConnRequest(id);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::queryConn() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Query KAS/MQ server for information on sessions
   * 
   * @param id
   *   The session ID or {@code "ALL"}
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage querySess(String id)
  {
    mLogger.trace("MqContextConnection::querySess() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQuerySessRequest(id);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::querySess() - OUT");
    return (MqStringMessage)reply;
  }

  /**
   * Query KAS/MQ server for information on configuration
   * 
   * @param type
   *   The configuration type
   * @return
   *   the message returned by the KAS/MQ server
   */
  public MqStringMessage queryConfig(EQueryConfigType type)
  {
    mLogger.trace("MqContextConnection::queryConfig() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryConfigRequest(type);
      reply = put(IMqConstants.cAdminQueueName, request);
      String resp;
      if (reply != null)
        resp = reply.getResponse().getDesc();
      else
        resp = "Connection to remote host was lost";
      logInfoAndSetResponse(resp);
    }
    
    mLogger.trace("MqContextConnection::queryConfig() - OUT");
    return (MqStringMessage)reply;
  }
  
  /**
   * Alter a new queue.
   * 
   * @param queue
   *   The queue name to be altered.
   * @param qProps
   *   The queue properties to be altered
   * @return
   *   {@code true} if queue was altered, {@code false} otherwise
   */
  public boolean alterQueue(String queue, Properties qProps)
  {
    mLogger.trace("MqContextConnection::alterQueue() - IN");
    
    String qname = queue.toUpperCase();
    IMqMessage request = MqRequestFactory.createAlterQueueRequest(qname, qProps);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::alterQueue() - OUT");
    return success;
  }

  /**
   * Terminate a connection
   * 
   * @param id
   *   The connection ID to terminate
   * @return
   *   the {@code true} if connection was terminated, {@code false} otherwise
   */
  public boolean termConn(UniqueId id)
  {
    mLogger.trace("MqContextConnection::termConn() - IN");
    
    IMqMessage request = MqRequestFactory.createTermConnRequest(id);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::termConn() - OUT");
    return success;
  }
  
  /**
   * Terminate a session
   * 
   * @param id
   *   The session ID to terminate
   * @return
   *   the {@code true} if session was terminated, {@code false} otherwise
   */
  public boolean termSess(UniqueId id)
  {
    mLogger.trace("MqContextConnection::termSess() - IN");
    
    IMqMessage request = MqRequestFactory.createTermSessRequest(id);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::termSess() - OUT");
    return success;
  }
  
  /**
   * Signal the KAS/MQ server it should shutdown
   * 
   * @return
   *   {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean termServer()
  {
    mLogger.trace("MqContextConnection::termServer() - IN");
    
    IMqMessage request = MqRequestFactory.createTermServerRequest(mUser);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.trace("MqContextConnection::termServer() - OUT");
    return success;
  }
  
  /**
   * Send an administrative request to KAS/MQ server.
   * 
   * @param request
   *   The request message
   * @return
   *   the reply message, if there is one.
   */
  protected IMqMessage requestReply(IMqMessage request)
  {
    mLogger.trace("MqContextConnection::requestReply() - IN");
    
    IMqMessage reply = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      reply = put(IMqConstants.cAdminQueueName, request);
    }
    
    mLogger.trace("MqContextConnection::requestReply() - OUT");
    return reply;
  }
  
  /**
   * Send an administrative request to KAS/MQ server and analyze response.<br>
   * The reply's description will be set to the latest response
   * 
   * @param request
   *   The request message
   * @return
   *   {@code true} if successful, {@code false} otherwise
   */
  protected boolean requestReplyAndAnalyze(IMqMessage request)
  {
    mLogger.trace("MqContextConnection::requestReplyAndAnalyze() - IN");
    
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
    
    mLogger.trace("MqContextConnection::requestReplyAndAnalyze() - OUT");
    return success;
  }
}

