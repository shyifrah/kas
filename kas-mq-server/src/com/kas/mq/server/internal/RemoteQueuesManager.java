package com.kas.mq.server.internal;

import java.util.Collection;
import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqClientImpl;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqRemoteQueue;
import com.kas.mq.typedef.QueueMap;

/**
 * The {@link RemoteQueuesManager} is the class that does the actual managing of remote queues for the {@link ServerRepository}
 * 
 * @author Pippo
 */
public class RemoteQueuesManager extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * A map of names to remotely defined queues
   */
  private QueueMap mRemoteQueues;
  
  /**
   * Construct the {@link RemoteQueuesManager}
   * 
   * @param config The {@link MqConfiguration configuration} object
   */
  RemoteQueuesManager(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mRemoteQueues = new QueueMap();
  }
  
  /**
   * Request from remote KAS/MQ servers their list of queues.
   */
  void sync()
  {
    mLogger.debug("RemoteQueuesManager::sync() - IN");
    
    for (Map.Entry<String, NetworkAddress> entry : mConfig.getRemoteManagers().entrySet())
    {
      String name = entry.getKey();
      NetworkAddress addr = entry.getValue();
      
      mLogger.debug("RemoteQueuesManager::sync() - Requesting queue list from qmgr \"" + name + "\" at " + addr.toString());
      String host = addr.getHost();
      int port = addr.getPort();
      
      MqClientImpl client = new MqClientImpl();
      client.connect(host, port);
      Properties props = client.queryQueue("*", true, false);
      if (props == null)
      {
        mLogger.warn(client.getResponse());
        continue;
      }
      
      mLogger.debug("RemoteQueuesManager::sync() - Received list: " + props.toPrintableString(0));
      
      MqManager mgr = new MqManager(name, host, port);
      int totalQueues = props.getIntProperty(IMqConstants.cKasPropertyQryqResultPrefix + ".total", 0);
      for (int i = 0; i < totalQueues; ++i)
      {
        String key = IMqConstants.cKasPropertyQryqResultPrefix + "." + i + ".name";
        String qname = props.getStringProperty(key, "");
        if (qname.length() > 0)
        {
          MqRemoteQueue queue = new MqRemoteQueue(mgr, name);
          mLogger.debug("RemoteQueuesManager::sync() - Adding to remote queues list queue: " + queue.toString());
          mRemoteQueues.put(qname, queue);
        }
      }
      
      client.disconnect();
    }
    
    mLogger.debug("RemoteQueuesManager::sync() - OUT");
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
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqRemoteQueue)mRemoteQueues.get(name);
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
    return mRemoteQueues.values();
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
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n");
    ///
    /// TODO: COMPLETE
    ///
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
