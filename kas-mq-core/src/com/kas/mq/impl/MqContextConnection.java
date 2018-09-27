package com.kas.mq.impl;

import com.kas.infra.base.Properties;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqConnection;
import com.kas.mq.impl.internal.MqRequestFactory;

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
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createDefineQueueRequest(queue, threshold);
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      success = reply.getResponse().getCode() == EMqCode.cOkay;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
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
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createDeleteQueueRequest(queue, force);
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      success = reply.getResponse().getCode() == EMqCode.cOkay;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
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
    
    MqStringMessage result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createQueryServerRequest(qType, qProps);
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      result = (MqStringMessage)reply;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextConnection::queryServer() - OUT");
    return result;
  }

  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqContextConnection::shutdown() - IN");
    
    boolean success = false;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage request = MqRequestFactory.createShutdownRequest(mUser);
      IMqMessage reply = put(IMqConstants.cAdminQueueName, request);
      success = reply.getResponse().getCode() == EMqCode.cOkay;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextConnection::shutdown() - OUT");
    return success;
  }
}
