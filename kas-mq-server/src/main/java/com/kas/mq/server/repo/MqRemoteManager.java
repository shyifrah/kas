package com.kas.mq.server.repo;

import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.internal.MqRemoteQueue;
import com.kas.mq.server.internal.MqServerConnectionPool;

/**
 * The {@link MqRemoteManager} is the class that does the actual managing of remote queues
 * owned by a specific KAS/MQ server
 * 
 * @author Pippo
 */
public class MqRemoteManager extends MqManager
{
  /**
   * Construct the {@link MqRemoteManager}
   * 
   * @param name
   *   The name of this manager
   * @param host
   *   The name (or IP address) of the host 
   * @param port
   *   The port to which this manager listens on
   */
  MqRemoteManager(String name, String host, int port)
  {
    super(name, host, port);
  }
  
  /**
   * Construct the queues map from the passed {@link Properties} object
   * 
   * @param props
   *   The {@link Properties} object that contains the queues definitions
   */
  public void setQueues(StringList qlist)
  {
    mLogger.trace("MqRemoteManager::setQueues() - IN, QList={}", qlist);
    
    if (qlist != null)
    {
      for (String qname : qlist)
      {
        if ((qname != null) && (qname.length() > 0))
        {
          MqRemoteQueue queue = new MqRemoteQueue(this, qname, MqServerConnectionPool.getInstance());
          mLogger.trace("MqRemoteManager::setQueues() - Adding to remote queues list queue: " + queue.toString());
          mQueues.put(qname, queue);
        }
      }
    }
    
    mLogger.trace("MqRemoteManager::setQueues() - OUT");
  }
  
  /**
   * Get a remote queue object.
   * 
   * @param name
   *   The name of the queue to be retrieved
   * @return
   *   the retrieved {@link MqRemoteQueue}
   */
  MqRemoteQueue getQueue(String name)
  {
    mLogger.trace("MqRemoteManager::getQueue() - IN, Name={}", name);
    MqRemoteQueue queue = null;
    
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = (MqRemoteQueue)mQueues.get(name);
      }
    }
    
    mLogger.trace("MqRemoteManager::getQueue() - OUT, Returns=[{}]", StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Add a remote queue object to the map.
   * 
   * @param name
   *   The name of the queue to be added
   * @return
   *   the added {@link MqRemoteQueue}
   */
  MqRemoteQueue addQueue(String name)
  {
    mLogger.trace("MqRemoteManager::addQueue() - IN, Name={}", name);
    
    MqRemoteQueue queue = null;
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = new MqRemoteQueue(this, name, MqServerConnectionPool.getInstance());
        mQueues.put(name, queue);
      }
    }
    
    mLogger.trace("MqRemoteManager::addQueue() - OUT, Returns=[{}]", StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Remove a remote queue object from the map
   * 
   * @param name
   *   The name of the queue to be removed
   * @return
   *   the removed {@link MqRemoteQueue}
   */
  MqRemoteQueue removeQueue(String name)
  {
    mLogger.trace("MqRemoteManager::removeQueue() - IN, Name={}", name);
    
    MqQueue queue = null;
    if (isActive())
    {
      if (name != null)
      {
        name = name.toUpperCase();
        queue = mQueues.remove(name);
      }
    }
    
    mLogger.trace("MqRemoteManager::removeQueue() - OUT, Returns=[{}]", StringUtils.asString(queue));
    return (MqRemoteQueue)queue;
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
    return super.toPrintableString(level);
  }
}
