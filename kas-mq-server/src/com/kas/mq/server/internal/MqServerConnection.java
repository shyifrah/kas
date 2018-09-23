package com.kas.mq.server.internal;

import com.kas.infra.base.Properties;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqConnection;
import com.kas.mq.impl.internal.MqRequestFactory;

/**
 * A {@link MqServerConnection} is an extended {@link MqConnection} used by server side
 * 
 * @author Pippo
 */
public class MqServerConnection extends MqConnection
{
  /**
   * Constructing the client
   */
  MqServerConnection()
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
    mLogger.debug("MqServerConnection::defineQueue() - IN");
    
    IMqMessage<?> request = MqRequestFactory.createDefineQueueRequest(queue, threshold);
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    boolean success = reply.getResponse().getCode() == EMqCode.cOkay;
    
    mLogger.debug("MqServerConnection::defineQueue() - OUT");
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
    mLogger.debug("MqServerConnection::deleteQueue() - IN");
    
    IMqMessage<?> request = MqRequestFactory.createDeleteQueueRequest(queue, force);
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    boolean success = reply.getResponse().getCode() == EMqCode.cOkay;
    
    mLogger.debug("MqServerConnection::deleteQueue() - OUT");
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
    mLogger.debug("MqServerConnection::queryQueue() - IN");
    
    Properties result = null;
    IMqMessage<?> request = MqRequestFactory.createQueryQueueRequest(name, prefix, all);
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    result = reply.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
    
    mLogger.debug("MqServerConnection::queryQueue() - OUT");
    return result;
  }

  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   */
  public boolean shutdown()
  {
    mLogger.debug("MqServerConnection::shutdown() - IN");
    
    IMqMessage<?> request = MqRequestFactory.createShutdownRequest(mUser);
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    boolean success = reply.getResponse().getCode() == EMqCode.cOkay;
    
    mLogger.debug("MqServerConnection::shutdown() - OUT");
    return success;
  }
  
  /**
   * Notify KAS/MQ server that the sender wishes to update its state
   * 
   * @param request The system-state change request. The message contains the new state of the sender.
   * @return the reply message from the receiver.
   */
  public IMqMessage<?> notifySysState(IMqMessage<?> request)
  {
    mLogger.debug("MqServerConnection::notifySysState() - IN");
    
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    
    mLogger.debug("MqServerConnection::notifySysState() - OUT");
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
    mLogger.debug("MqServerConnection::notifyRepoUpdate() - IN");
    
    IMqMessage<?> request = MqRequestFactory.createRepositoryUpdateMessage(qmgr, queue, added);
    IMqMessage<?> reply = put(IMqConstants.cAdminQueueName, request);
    boolean success = reply.getResponse().getCode() == EMqCode.cOkay;
    
    mLogger.debug("MqServerConnection::notifyRepoUpdate() - OUT");
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
