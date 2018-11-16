package com.kas.mq.server.internal;

import java.util.Collection;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqRequestFactory;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.MqRemoteManager;

public class ServerNotifier extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
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
   * @param repository The {@link IRepository server repository}
   */
  public ServerNotifier(IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
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
    mLogger.debug("ServerNotifier::notifyServerActivated() - IN");
    
    String qmgr = mRepository.getLocalManager().getName();
    IMqMessage message = MqRequestFactory.createSystemStateMessage(qmgr, true);
    
    Properties props = mRepository.queryLocalQueues("", true, false);
    message.setSubset(props);
    
    notify(message, true);
    
    mLogger.debug("ServerNotifier::notifyServerActivated() - OUT");
  }
  
  /**
   * Notify remote KAS/MQ servers that this KAS/MQ server was deactivated.<br>
   * We include in the notification message the list of session IDs so the receiver
   * of the message can terminate these sessions from its side, thus making it easier
   * on this server to terminate gracefully.
   */
  public void notifyServerDeactivated()
  {
    mLogger.debug("ServerNotifier::notifyServerDeactivated() - IN");
    
    String qmgr = mRepository.getLocalManager().getName();
    IMqMessage message = MqRequestFactory.createSystemStateMessage(qmgr, false);
    
    notify(message, false);
    
    mLogger.debug("ServerNotifier::notifyServerDeactivated() - OUT");
  }
  
  /**
   * Send the specified message to remote KAS/MQ servers.<br>
   * <br>
   * Note that since the sent message is a notification message (this is not verified),
   * the queue name is not tested because the {@link SessionHandler} will handle the message
   * prior to assuming it should be put into a queue.
   * 
   * @param message The {@link IMqMessage} that will be sent to each and every remote address
   * @return the reply message received from receiver 
   */
  private void notify(IMqMessage message, boolean activate)
  {
    mLogger.debug("ServerNotifier::notify() - IN");
    
    Collection<MqRemoteManager> remoteManagers = mRepository.getRemoteManagers();
    for (MqRemoteManager remoteManager : remoteManagers)
    {
      String name = remoteManager.getName();
      String host = remoteManager.getHost();
      int port = remoteManager.getPort();
      
      mLogger.debug("ServerNotifier::notify() - Notifying KAS/MQ server \"" + name + "\" (" + host + ':' + port + ") on server state change");
      MqServerConnection conn = mPool.allocate();
      
      conn.connect(host, port);
      if (conn.isConnected())
      {
        conn.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
        
        IMqMessage reply = conn.notifySysState(message);
        
        Properties remoteQueues = reply.getSubset(IMqConstants.cKasPropertyQueryResultPrefix);
        if (!remoteManager.isActive())
        {
          remoteManager.activate();
          remoteManager.setQueues(remoteQueues);
        }
        
        conn.disconnect();
      }
      
      mPool.release(conn);
    }
    
    mLogger.debug("ServerNotifier::notify() - OUT");
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
