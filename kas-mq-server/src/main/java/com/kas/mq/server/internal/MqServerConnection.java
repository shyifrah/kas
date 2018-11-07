package com.kas.mq.server.internal;

import com.kas.mq.impl.MqContextConnection;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.MqRequestFactory;

/**
 * A {@link MqServerConnection} is an extended {@link MqContextConnection} used by server side.
 * It enhances the functionality provided by {@link MqContextConnection} by adding two more functions
 * for notifying remote KAS/MQ servers that a server has changed its state from active to inactive
 * (or vice versa) or that the local server's repository was updated (defined/deleted queue).
 * 
 * @author Pippo
 */
public class MqServerConnection extends MqContextConnection
{
  /**
   * Constructing the connection
   * 
   * @param clientName The client application name
   */
  MqServerConnection(String clientName)
  {
    super(clientName);
  }
  
  /**
   * Notify KAS/MQ server that the sender wishes to update its state
   * 
   * @param request The system-state change request. The message contains the new state of the sender.
   * @return the reply message from the receiver.
   */
  public IMqMessage notifySysState(IMqMessage request)
  {
    mLogger.debug("MqServerConnection::notifySysState() - IN");
    
    IMqMessage reply = requestReply(request);
    
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
    
    IMqMessage request = MqRequestFactory.createRepositoryUpdateMessage(qmgr, queue, added);
    boolean success = requestReplyAndAnalyze(request);
    
    mLogger.debug("MqServerConnection::notifyRepoUpdate() - OUT");
    return success;
  }
}
