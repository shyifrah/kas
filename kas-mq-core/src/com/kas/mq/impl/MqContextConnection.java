package com.kas.mq.impl;

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
   * Constructing the client
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
      IMqMessage<?> request = MqRequestFactory.createDefineQueueRequest(queue, threshold);
      IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
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
      IMqMessage<?> request = MqRequestFactory.createDeleteQueueRequest(queue, force);
      IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
      success = reply.getResponse().getCode() == EMqCode.cOkay;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextConnection::deleteQueue() - OUT");
    return success;
  }
  
  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name.
   * @param prefix If {@code true}, then {@code name} designates a queue name prefix.
   * If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues
   * @param outProps If {@code true}, the output of the query is returned
   * only on top of the reply message's Properties. if {@code false}, the output is returned
   * by means of the reply message's Properties <b>and</b> message body (formatted text). 
   * @return the reply message returned from the server.
   */
  public MqTextMessage queryQueue(String name, boolean prefix, boolean all, boolean outProps)
  {
    mLogger.debug("MqContextConnection::queryQueue() - IN");
    
    MqTextMessage result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createQueryQueueRequest(name, prefix, all, outProps);
      IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
      result = (MqTextMessage)reply;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextConnection::queryQueue() - OUT");
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
      IMqMessage<?> request = MqRequestFactory.createShutdownRequest(mUser);
      IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
      success = reply.getResponse().getCode() == EMqCode.cOkay;
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextConnection::shutdown() - OUT");
    return success;
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
