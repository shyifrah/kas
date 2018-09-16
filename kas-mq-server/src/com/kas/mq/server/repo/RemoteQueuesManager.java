package com.kas.mq.server.repo;

import java.util.Collection;
import java.util.Map;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqClientImpl;
//import com.kas.mq.impl.internal.MqClientImpl;
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
   * KAS/MQ server configuration
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

    mActive = true;

    mLogger.debug("RemoteQueuesManager::activate() - OUT");
  }
  
  /**
   * Construct the queues map from the passed {@link Properties} object
   * 
   * @param props The {@link Properties} object that contains the queues definitions
   */
  public void setQueues(Properties props)
  {
    mLogger.debug("RemoteQueuesManager::setQueues() - IN");
    
    for (Map.Entry<Object, Object> entry : props.entrySet())
    {
      String key = (String)entry.getKey();
      String qname = key.substring(IMqConstants.cKasPropertyQryqResultPrefix.length()+1);
      if (qname.length() > 0)
      {
        MqRemoteQueue queue = new MqRemoteQueue(this, qname);
        mLogger.debug("RemoteQueuesManager::setQueues() - Adding to remote queues list queue: " + queue.toString());
        mQueues.put(qname, queue);
      }
    }
    
    mLogger.debug("RemoteQueuesManager::setQueues() - OUT");
  }
  
  /**
   * Query queues
   * 
   * @param name The queue name/prefix.
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   */
  Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("RemoteQueuesManager::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    Properties props = new Properties();
    for (MqQueue queue : mQueues.values())
    {
      MqRemoteQueue mqrq = (MqRemoteQueue)queue;
      boolean include = false;
      if (prefix)
        include = mqrq.getName().startsWith(name);
      else
        include = mqrq.getName().equals(name);
      
      mLogger.debug("RemoteQueuesManager::queryQueue() - Checking if current queue [" + mqrq.getName() + "] matches query: " + include);
      if (include)
      {
        String key = IMqConstants.cKasPropertyQryqResultPrefix + "." + mqrq.getName();
        props.setStringProperty(key, mqrq.queryResponse(all));
      }
    }
    
    mLogger.debug("RemoteQueuesManager::queryQueue() - OUT, Returns=" + props.size() + " queues");
    return props;
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
   * Add a remote queue object to the map.
   * 
   * @param name The name of the queue to be added
   * @return the added {@link MqRemoteQueue}
   */
  MqRemoteQueue addQueue(String name)
  {
    mLogger.debug("RemoteQueuesManager::addQueue() - IN, Name=" + name);
    
    MqRemoteQueue queue = null;
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = new MqRemoteQueue(this, name);
        mQueues.put(name, queue);
      }
    }
    
    mLogger.debug("RemoteQueuesManager::addQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Remove a remote queue object from the map
   * 
   * @param name The name of the queue to be removed
   * @return the removed {@link MqRemoteQueue}
   */
  MqRemoteQueue removeQueue(String name)
  {
    mLogger.debug("RemoteQueuesManager::removeQueue() - IN, Name=" + name);
    
    MqQueue queue = null;
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = mQueues.remove(name);
      }
    }
    
    mLogger.debug("RemoteQueuesManager::removeQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return (MqRemoteQueue)queue;
  }
  
  /**
   * Notify remote KAS/MQ server that the local repository was updated
   * 
   * @param name The name of the queue that was subject to the update
   * @param added If {@code true}, then queue {@code name} was added, otherwise, it was removed
   */
  void notifyRepositoryUpdated(String name, boolean added)
  {
    mLogger.debug("RemoteQueuesManager::notifyLocalQueueAdded() - IN, Name=" + name);
    
    MqClientImpl client = new MqClientImpl();
    client.connect(mHost, mPort);
    if (client.isConnected())
    {
      client.notifyRepoUpdate(mConfig.getManagerName(), name, added);
    }
    client.disconnect();
    
    mLogger.debug("RemoteQueuesManager::notifyLocalQueueAdded() - OUT");
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
