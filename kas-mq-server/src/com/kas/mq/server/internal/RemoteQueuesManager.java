package com.kas.mq.server.internal;

import java.util.Collection;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqClientImpl;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqRemoteQueue;

/**
 * The {@link RemoteQueuesManager} is the class that does the actual managing of remote queues
 * owned by a specific KAS/MQ server
 * 
 * @author Pippo
 */
public class RemoteQueuesManager extends MqManager
{
  /**
   * KAS/MQ server's configuration object
   */
  private MqConfiguration mConfig;
  
  /**
   * Construct the {@link RemoteQueuesManager}
   * 
   * @param config The {@link MqConfiguration configuration} object
   */
  RemoteQueuesManager(MqConfiguration config, String name)
  {
    super(name, config.getRemoteManagers().get(name).getHost(), config.getRemoteManagers().get(name).getPort());
    mConfig = config;
  }
  
  /**
   * Activate the {@link RemoteQueuesManager}: request from the corresponding remote KAS/MQ server its list of queues.
   */
  public void activate()
  {
    mLogger.debug("RemoteQueuesManager::activate() - IN");
    
    mLogger.debug("RemoteQueuesManager::activate() - Requesting queue list from qmgr \"" + mName + "\" at " + mHost + ':' + mPort);
    
    MqClientImpl client = new MqClientImpl();
    client.connect(mHost, mPort);
    
    if (client.isConnected())
    {
      mActive = true;
      
      Properties props = client.queryQueue(mConfig.getManagerName(), "", true, false);
      if (props == null)
        mLogger.warn(client.getResponse());
      
      mLogger.debug("RemoteQueuesManager::activate() - Received list: " + props.toPrintableString(0));
      
      int totalQueues = props.getIntProperty(IMqConstants.cKasPropertyQryqResultPrefix + ".total", 0);
      for (int i = 0; i < totalQueues; ++i)
      {
        String key = IMqConstants.cKasPropertyQryqResultPrefix + "." + (i+1) + ".name";
        String qname = props.getStringProperty(key, "");
        if (qname.length() > 0)
        {
          MqRemoteQueue queue = new MqRemoteQueue(this, qname);
          mLogger.debug("RemoteQueuesManager::activate() - Adding to remote queues list queue: " + queue.toString());
          mQueues.put(qname, queue);
        }
      }
    }

    client.disconnect();
    
    mLogger.debug("RemoteQueuesManager::activate() - OUT");
  }
  
  /**
   * Get a remote queue object.
   * 
   * @param name The name of the queue to be retrieved
   * @return the retrieved {@link MqRemoteQueue}
   */
  MqRemoteQueue getQueue(String name)
  {
    mLogger.debug("RemoteQueuesManager::getQueue() - IN, Name=" + name);
    MqRemoteQueue queue = null;
    
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = (MqRemoteQueue)mQueues.get(name);
      }
    }
    
    mLogger.debug("RemoteQueuesManager::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return a collection of all local queues
   */
  Collection<MqQueue> getAll()
  {
    mLogger.debug("RemoteQueuesManager::getAll() - IN");
    
    Collection<MqQueue> result = null;
    if (isActive())
    {
      result = mQueues.values();
    }
    
    mLogger.debug("RemoteQueuesManager::getAll() - IN");
    return result;
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
    return super.toPrintableString(level);
  }
}
