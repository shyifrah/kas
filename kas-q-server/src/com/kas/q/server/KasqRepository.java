package com.kas.q.server;

import java.io.IOException;
import java.net.ServerSocket;
import javax.jms.Queue;
import javax.jms.Topic;
import com.kas.containers.CappedContainerProxy;
import com.kas.containers.CappedContainersFactory;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.ILocator;
import com.kas.q.impl.KasqQueue;
import com.kas.q.server.internal.MessagingConfiguration;

public class KasqRepository extends KasObject implements IInitializable, ILocator
{
  /***************************************************************************************************************
   * 
   */
  private ILogger mLogger;
  private MessagingConfiguration mConfig;
  private ServerSocket mLocatorSocket;
  
  private CappedContainerProxy mQueuesMapProxy;
  private CappedHashMap<String, KasqQueue> mQueuesMap;
  
  //private CappedContainerProxy mManagersMapProxy;
  //private CappedHashMap<String, KasqManager> mManagersMap;
  
  /***************************************************************************************************************
   * Construct the repository, specifying the Messaging configuration object.
   * 
   * @param config the {@code MessagingConfiguration} object
   */
  @SuppressWarnings("unchecked")
  KasqRepository(MessagingConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    
    mQueuesMapProxy = new CappedContainerProxy("messaging.queues.map", mLogger);
    mQueuesMap = (CappedHashMap<String, KasqQueue>)CappedContainersFactory.createMap(mQueuesMapProxy);
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init() 
  {
    mLogger.debug("KasqRepository::init() - IN");
    boolean success = true;
    
    // define deadq
    defineQueue(mConfig.getDeadQueue());
    
    // define adminq
    defineQueue(mConfig.getAdminQueue());
    
    try
    {
      mLocatorSocket = new ServerSocket(mConfig.getLocatorPort());
    }
    catch (IOException e)
    {
      success = false;
    }
    
    mLogger.debug("KasqRepository::init() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    mLogger.debug("KasqRepository::term() - IN");
    
    for (KasqQueue queue : mQueuesMap.values())
    {
      String qname = "unknown-queue";
      try
      {
        qname = queue.getQueueName();
      }
      catch (Throwable e) {}
      mLogger.debug("KasqRepository::term() - Writting queue contents. Queue=[" + qname + "]; Messages=[" + queue.getSize() + "]");
      queue.term();
    }
    
    try
    {
      mLocatorSocket.close();
    }
    catch (IOException e) {}
    
    mLogger.debug("KasqRepository::term() - OUT");
    return true;
  }
  
  /***************************************************************************************************************
   * Define and initialize a queue in the repository, specifying the name of the queue
   * 
   * @param name the name of the queue
   * 
   * @return true if queue definition was successful
   */
  private boolean defineQueue(String name)
  {
    return defineQueue(name, mConfig.getManagerName());
  }
  
  /***************************************************************************************************************
   * Define and initialize a queue in the repository, specifying the name of the queue and the its manager
   * 
   * @param name the name of the queue
   * @param name of the manager in which this queue is defined
   * 
   * @return true if queue definition was successful
   */
  private boolean defineQueue(String name, String managerName)
  {
    mLogger.debug("KasqRepository::defineQueue() - IN");
    boolean success = true;
    
    mLogger.info("Define queue with name=[" + name + "] at manager=[" + managerName + "]");
    
    KasqQueue queue = mQueuesMap.get(name);
    if (queue == null)
    {
      try
      {
        queue = new KasqQueue(name, managerName);
        success = queue.init();
        
        if (success) mQueuesMap.put(name, queue);
      }
      catch (Throwable e)
      {
        mLogger.error("Failed to define local queue [" + name + "] at manager=[" + managerName + "]. Exception caught: ", e);
        success = false;
      }
    }
    
    mLogger.debug("KasqRepository::defineLocalQueue() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized Queue locateQueue(String name)
  {
    return mQueuesMap.get(name);
  }

  /***************************************************************************************************************
   * 
   */
  public synchronized Topic locateTopic(String name)
  {
    return null;
  }

  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
