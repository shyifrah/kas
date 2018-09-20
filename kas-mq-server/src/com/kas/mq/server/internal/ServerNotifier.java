package com.kas.mq.server.internal;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqClientImpl;
import com.kas.mq.impl.internal.MqRequestFactory;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.RemoteQueuesManager;

public class ServerNotifier extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * KAS/MQ server configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * Server's repository
   */
  private IRepository mRepository;
  
  /**
   * Construct a {@link ServerNotifier server notifier}, specifying the associated {@link MqConfiguration}
   * 
   * @param config The associated {@link MqConfiguration}
   * @param controller The session controller
   */
  public ServerNotifier(MqConfiguration config, IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mRepository = repository;
  }
  
  /**
   * Notify remote KAS/MQ servers that this KAS/MQ server was activated.<br>
   * We include in the notification message the list of local queues so the receiver
   * of the message can add and make them available for its clients.
   */
  public void notifyServerActivated()
  {
    mLogger.debug("ServerNotifier::notifyServerActivated() - IN");
    
    String qmgr = mConfig.getManagerName();
    IMqMessage<?> message = MqRequestFactory.createSystemStateMessage(qmgr, true);
    
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
    
    String qmgr = mConfig.getManagerName();
    IMqMessage<?> message = MqRequestFactory.createSystemStateMessage(qmgr, false);
    
//    Properties props = new Properties();
//    for (Map.Entry<UniqueId, SessionHandler> entry : mController.getHandlers().entrySet())
//    {
//      UniqueId uid = entry.getKey();
//      String key = IMqConstants.cKasPropertySyssSessionPrefix + "." + uid.toString();
//      props.setStringProperty(key, uid.toString());
//    }
//    message.setSubset(props);
//    
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
   */
  private void notify(IMqMessage<?> message, boolean activate)
  {
    mLogger.debug("ServerNotifier::notify() - IN");
    
    Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
    
    for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
    {
      String remoteQmgrName  = entry.getKey();
      NetworkAddress address = entry.getValue();
      
      mLogger.debug("ServerNotifier::notify() - Notifying KAS/MQ server \"" + remoteQmgrName + "\" (" + address.toString() + ") on server state change");
      
      MqClientImpl client = new MqClientImpl();
      client.connect(address.getHost(), address.getPort());
      if (client.isConnected())
      {
        IMqMessage<?> reply = client.notifySysState(message, activate);
        if (reply != null)
        {
          Properties remoteQueues = reply.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
          RemoteQueuesManager rqmgr = (RemoteQueuesManager)mRepository.getRemoteManager(remoteQmgrName);
          if (!rqmgr.isActive())
          {
            rqmgr.activate();
            rqmgr.setQueues(remoteQueues);
          }
        }
        client.disconnect();
      }
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
