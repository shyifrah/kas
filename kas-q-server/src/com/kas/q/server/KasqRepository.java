package com.kas.q.server;

import java.io.IOException;
import java.net.ServerSocket;
import com.kas.containers.CappedContainerProxy;
import com.kas.containers.CappedContainersFactory;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.WeakRef;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.server.internal.MessagingConfiguration;

public class KasqRepository extends AKasObject implements IInitializable
{
  /***************************************************************************************************************
   * 
   */
  private ILogger mLogger;
  private MessagingConfiguration mConfig;
  private ServerSocket mLocatorSocket;
  
  private CappedContainerProxy mQueuesMapProxy;
  private CappedHashMap<String, KasqQueue> mQueuesMap;
  private CappedContainerProxy mTopicsMapProxy;
  private CappedHashMap<String, KasqTopic> mTopicsMap;
  
  private WeakRef<KasqQueue> mDeadQueue;
  private WeakRef<KasqQueue> mAdminQueue;
  
  /***************************************************************************************************************
   * Construct the repository, specifying the Messaging configuration object.
   * 
   * @param config the {@code MessagingConfiguration} object
   */
  @SuppressWarnings("unchecked")
  KasqRepository()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = KasqServer.getInstance().getConfiguration();
    
    mQueuesMapProxy = new CappedContainerProxy("messaging.queues.map", mLogger);
    mQueuesMap = (CappedHashMap<String, KasqQueue>)CappedContainersFactory.createMap(mQueuesMapProxy);
    
    mTopicsMapProxy = new CappedContainerProxy("messaging.topics.map", mLogger);
    mTopicsMap = (CappedHashMap<String, KasqTopic>)CappedContainersFactory.createMap(mTopicsMapProxy);
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init() 
  {
    mLogger.debug("KasqRepository::init() - IN");
    boolean success = true;
    
    // define deadq
    String deadq = mConfig.getDeadQueue();
    defineQueue(deadq);
    mDeadQueue = new WeakRef<KasqQueue>(mQueuesMap.get(deadq));
    
    // define adminq
    String adminq = mConfig.getAdminQueue();
    defineQueue(adminq);
    mAdminQueue = new WeakRef<KasqQueue>(mQueuesMap.get(adminq));
    
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
      mLogger.debug("KasqRepository::term() - Writing queue contents. Queue=[" + qname + "]; Messages=[" + queue.size() + "]");
      queue.term();
    }
    
    for (KasqTopic topic : mTopicsMap.values())
    {
      String tname = "unknown-topic";
      try
      {
        tname = topic.getTopicName();
      }
      catch (Throwable e) {}
      mLogger.debug("KasqRepository::term() - Writing topic contents. Topic=[" + tname + "]; Messages=[" + topic.size() + "]");
      topic.term();
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
  public boolean defineQueue(String name)
  {
    return defineQueue(name, mConfig.getManagerName());
  }
  
  /***************************************************************************************************************
   * Define and initialize a queue in the repository, specifying the name of the queue and the its manager
   * 
   * @param name the name of the queue
   * @param managerName of the manager in which this queue is defined
   * 
   * @return true if queue definition was successful
   */
  public boolean defineQueue(String name, String managerName)
  {
    return define(name, managerName, false);
  }
  
  /***************************************************************************************************************
   * Define and initialize a topic in the repository, specifying the name of the topic
   * 
   * @param name the name of the topic
   * 
   * @return true if topic definition was successful
   */
  public boolean defineTopic(String name)
  {
    return defineTopic(name, mConfig.getManagerName());
  }
  
  /***************************************************************************************************************
   * Define and initialize a topic in the repository, specifying the name of the topic and the its manager
   * 
   * @param name the name of the topic
   * @param managerName of the manager in which this topic defined
   * 
   * @return true if topic definition was successful
   */
  public boolean defineTopic(String name, String managerName)
  {
    return define(name, managerName, true);
  }
  
  /***************************************************************************************************************
   * Define and initialize a topic/queue in the repository, specifying the name of the topic/queue and the its manager
   * 
   * @param name the name of the topic/queue
   * @param managerName of the manager in which this topic/queue defined
   * @param isTopic true if the defined object is a Topic, false if it's a Queue
   * 
   * @return true if topic/queue definition was successful
   */
  private boolean define(String name, String managerName, boolean isTopic)
  {
    mLogger.debug("KasqRepository::define() - IN");
    boolean success = true;
    
    mLogger.info("Define " + (isTopic ? "topic" : "queue") + " with name=[" + name + "] at manager=[" + managerName + "]");
    
    IKasqDestination dest = (isTopic ? mTopicsMap.get(name) : mQueuesMap.get(name));
    if (dest == null)
    {
      try
      {
        if (isTopic)
        {
          KasqTopic topic = new KasqTopic(name, managerName);
          success = topic.init();
        
          if (success) mTopicsMap.put(name, topic);
        }
        else
        {
          KasqQueue queue = new KasqQueue(name, managerName);
          success = queue.init();
          
          if (success) mQueuesMap.put(name, queue);
        }
      }
      catch (Throwable e)
      {
        mLogger.error("Failed to define local topic [" + name + "] at manager=[" + managerName + "]. Exception caught: ", e);
        success = false;
      }
    }
    
    mLogger.debug("KasqRepository::define() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * Gets the dead queue
   * 
   * @return the {@code KasqQueue} object of the dead queue
   */
  public KasqQueue getDeadQueue()
  {
    return mDeadQueue.get();
  }
  
  /***************************************************************************************************************
   * Gets the admin queue
   * 
   * @return the {@code KasqQueue} object of the admin queue
   */
  public KasqQueue getAdminQueue()
  {
    return mAdminQueue.get();
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized KasqQueue locateQueue(String name)
  {
    return mQueuesMap.get(name);
  }

  /***************************************************************************************************************
   * 
   */
  public synchronized KasqTopic locateTopic(String name)
  {
    return mTopicsMap.get(name);
  }
  
  /***************************************************************************************************************
   * Locate a {@code IKasqDestination} object based on it's name. First we look in the queues map,
   * then in the topics map.
   * 
   * @param name the name of the destination
   * 
   * @return {@code IKasqDestination} that was located, or null if none was found. 
   */
  public IKasqDestination locate(String name)
  {
    IKasqDestination iDest = locateQueue(name);
    return (iDest != null ? iDest : locateTopic(name));
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Queues=(").append(mQueuesMap.toPrintableString(level + 1)).append(")\n")
      .append(pad).append("  Topics=(").append(mTopicsMap.toPrintableString(level + 1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
