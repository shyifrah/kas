package com.kas.mq.server.mgr;

import com.kas.infra.base.KasException;
import com.kas.mq.impl.internal.IClient;
import com.kas.mq.impl.internal.MqClientImpl;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqLocalQueue;

public class RemoteManager extends MqManager
{
  private IClient mClient;
  
  public RemoteManager(String name, String host, int port) throws KasException
  {
    super(name, host, port);
    mClient = new MqClientImpl();
    mClient.connect(host, port);
  }
  
  /**
   * Add a queue to the local queues map
   * 
   * @param queue The {@link MqLocalQueue} to add
   */
  public boolean createQueue(MqLocalQueue queue)
  {
    mLogger.debug("RemoteManager::createQueue() - IN");
    
    boolean success = super.createQueue(queue);
    
    synchronize();
    
    mLogger.debug("RemoteManager::createQueue() - Queue with name " + queue.getName() + (success ? " was successfully created" : " already exist"));
    mLogger.debug("RemoteManager::createQueue() - OUT");
    return success;
  }
  
  /**
   * Remove a queue from the local queues map
   * 
   * @param name The {@link MqLocalQueue} name to remove
   */
  public boolean removeQueue(String name)
  {
    mLogger.debug("RemoteManager::removeQueue() - IN, Name=" + name);
    
    boolean success = super.removeQueue(name);
    
    synchronize();
    
    mLogger.debug("RemoteManager::removeQueue() - Queue with name " + name + (success ? " was successfully removed" : " does not exist"));
    mLogger.debug("RemoteManager::removeQueue() - OUT");
    return success;
  }
  
  private void synchronize()
  {
    
  }
}
