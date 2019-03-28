package com.kas.mq.server.internal;

import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.typedef.StringList;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqRequestFactory;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.MqRemoteManager;

/**
 * The {@link ServerNotifier} responsible for broadcasting messages
 * to remote KAS/MQ servers regarding the local KAS/MQ server state.
 * 
 * @author Pippo
 */
public class ServerNotifier extends AKasObject
{
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * Server's repository
   */
  private IRepository mRepository;
  
  /**
   * Connection pool
   */
  private MqServerConnectionPool mPool;
  
  /**
   * Construct a {@link ServerNotifier server notifier}, specifying the {@link IRepository}
   * 
   * @param repository
   *   The {@link IRepository server repository}
   */
  public ServerNotifier(IRepository repository)
  {
    mLogger = LogManager.getLogger(getClass());
    mRepository = repository;
    mPool = MqServerConnectionPool.getInstance();
  }
  
  /**
   * Notify remote KAS/MQ servers that this KAS/MQ server was activated.<br>
   * We include in the notification message the list of local queues so the receiver
   * of the message can add and make them available for its clients.
   */
  public void notifyServerActivated()
  {
    mLogger.trace("ServerNotifier::notifyServerActivated() - IN");
    
    String qmgr = mRepository.getLocalManager().getName();
    IMqMessage message = MqRequestFactory.createSystemStateMessage(qmgr, true);
    
    StringList qlist = mRepository.queryLocalQueues("", true, false);
    message.setStringProperty(IMqConstants.cKasPropertySyssQueueList, qlist.toString());
    
    try
    {
      notify(message, true);
    }
    catch (Throwable e)
    {
      mLogger.warn("Failed to notify remote KAS/MQ servers on server activation");
    }
    
    mLogger.trace("ServerNotifier::notifyServerActivated() - OUT");
  }
  
  /**
   * Notify remote KAS/MQ servers that this KAS/MQ server was deactivated.<br>
   * We include in the notification message the list of session IDs so the receiver
   * of the message can terminate these sessions from its side, thus making it easier
   * on this server to terminate gracefully.
   */
  public void notifyServerDeactivated()
  {
    mLogger.trace("ServerNotifier::notifyServerDeactivated() - IN");
    
    String qmgr = mRepository.getLocalManager().getName();
    IMqMessage message = MqRequestFactory.createSystemStateMessage(qmgr, false);
    
    try
    {
      notify(message, false);
    }
    catch (Throwable e)
    {
      mLogger.warn("Failed to notify remote KAS/MQ servers on server deactivation");
    }
    
    mLogger.trace("ServerNotifier::notifyServerDeactivated() - OUT");
  }
  
  /**
   * Send the specified message to remote KAS/MQ servers.<br>
   * Note that since the sent message is a notification message (this is not verified),
   * the queue name is not tested because the {@link SessionHandler} will handle the message
   * prior to assuming it should be put into a queue.
   * 
   * @param message
   *   The {@link IMqMessage} that will be sent to each and every remote address
   * @return
   *   the reply message received from receiver 
   */
  private void notify(IMqMessage message, boolean activate)
  {
    mLogger.trace("ServerNotifier::notify() - IN");
    
    Collection<MqRemoteManager> remoteManagers = mRepository.getRemoteManagers();
    for (MqRemoteManager remoteManager : remoteManagers)
    {
      String name = remoteManager.getName();
      String host = remoteManager.getHost();
      int port = remoteManager.getPort();
      
      mLogger.trace("ServerNotifier::notify() - Notifying KAS/MQ server \"" + name + "\" (" + host + ':' + port + ") on server state change");
      MqServerConnection conn = mPool.allocate();
      
      conn.connect(host, port);
      if (conn.isConnected())
      {
        boolean logged = conn.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
        if (!logged) throw new IllegalStateException("Failed to login to remote KAS/MQ server at " + host + ':' + port);
        
        IMqMessage reply = conn.notifySysState(message);
        
        String remoteQueues = reply.getStringProperty(IMqConstants.cKasPropertySyssQueueList, null);
        if (remoteQueues != null)
        {
          StringList remoteQueueList = StringList.fromString(remoteQueues);
          remoteManager.setQueues(remoteQueueList);
        }
        
        conn.disconnect();
      }
      
      mPool.release(conn);
    }
    
    mLogger.trace("ServerNotifier::notify() - OUT");
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
