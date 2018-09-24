package com.kas.mq.impl;

import com.kas.infra.base.Properties;
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
  MqContextConnection()
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
    mLogger.debug("MqContextDelegator::defineQueue() - IN");
    
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
    
    mLogger.debug("MqContextDelegator::defineQueue() - OUT");
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
    mLogger.debug("MqContextDelegator::deleteQueue() - IN");
    
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
    
    mLogger.debug("MqContextDelegator::deleteQueue() - OUT");
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
    mLogger.debug("MqContextDelegator::queryQueue() - IN");
    
    Properties result = null;
    if (!isConnected())
    {
      logErrorAndSetResponse("Not connected to host");
    }
    else
    {
      IMqMessage<?> request = MqRequestFactory.createQueryQueueRequest(name, prefix, all);
      IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
      result = reply.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
      logInfoAndSetResponse(reply.getResponse().getDesc());
    }
    
    mLogger.debug("MqContextDelegator::queryQueue() - OUT");
    return result;
  }

  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqContextDelegator::shutdown() - IN");
    
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
    
    mLogger.debug("MqContextDelegator::shutdown() - OUT");
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
