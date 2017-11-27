package com.kas.q.server;

import java.io.File;
import com.kas.containers.CappedContainerProxy;
import com.kas.containers.CappedContainersFactory;
import com.kas.containers.CappedHashMap;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.WeakRef;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.server.internal.MessagingConfiguration;

public class KasqRepository extends AKasObject implements IInitializable
{
  /***************************************************************************************************************
   * 
   */
  private ILogger mLogger;
  private MessagingConfiguration mConfig;
  
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
    
    mLogger.trace("KasqRepository::init() - Initializing queues and topics...");
    
    // read repo directory contents and define a queue/topic per backup file found there
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("KasqRepository::init() - Repository directory does not exist, try to create. result=" + success);
    }
    else
    if (!repoDir.isDirectory())
    {
      success = false;
      mLogger.error("KasqRepository::init() - Repository directory path points to a file. Cannot continue");
    }
    else
    {
      String [] entries = repoDir.list();
      for (String entry : entries)
      {
        EDestinationType destType;
        if (entry.endsWith(".qbk"))
          destType = EDestinationType.cQueue;
        else if (entry.endsWith(".tbk"))
          destType = EDestinationType.cTopic;
        else
          continue;
        
        File destFile = new File(repoDirPath + File.separatorChar + entry);
        if (destFile.isFile())
        {
          String destName = entry.substring(0, entry.lastIndexOf('.'));
          mLogger.trace("KasqRepository::init() - Restoring contents of " + destType.toString() + " [" + destName + ']');
          define(destName, mConfig.getManagerName(), destType);
        }
      }
      
      // define deadq if needed
      if (locateQueue(mConfig.getDeadQueue()) == null)
      {
        String deadq = mConfig.getDeadQueue();
        mLogger.trace("KasqRepository::init() - DEADQ: [" + deadq + "] could not be located, define it now");
        
        defineQueue(deadq);
        mDeadQueue = new WeakRef<KasqQueue>(mQueuesMap.get(deadq));
      }
      
      // define adminq if needed
      if (locateQueue(mConfig.getAdminQueue()) == null)
      {
        String adminq = mConfig.getAdminQueue();
        mLogger.trace("KasqRepository::init() - ADMINQ: [" + adminq + "] could not be located, define it now");
        
        defineQueue(adminq);
        mDeadQueue = new WeakRef<KasqQueue>(mQueuesMap.get(adminq));
      }
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
    return define(name, managerName, EDestinationType.cQueue);
  }
  
  /***************************************************************************************************************
   * Define a destination to the repository.
   * If the destination is already defined in the repository, we don't change it.
   * 
   * @param destination the destination to be added to the repository
   * 
   * @return true if the destination was successfully added, false otherwise
   */
  public boolean define(IKasqDestination destination)
  {
    mLogger.debug("KasqRepository::define() - IN");
    boolean success = true;
    
    if (destination == null)
    {
      success = false;
    }
    else
    {
      mLogger.info("Define " + destination.getType() + " with name=[" + destination.getName() + "]");
      if (destination.getType() == EDestinationType.cQueue)
      {
        KasqQueue q = mQueuesMap.get(destination.getName());
        if (q == null)
        {
          mLogger.debug("KasqRepository::define() - Queue with name " + destination.getName() + " does not exist. Define it now");
          mQueuesMap.put(destination.getName(), (KasqQueue)destination);
          destination.init();
        }
        else
        {
          mLogger.debug("KasqRepository::define() - Queue with name " + destination.getName() + " already exists");
          success = false;
        }
      }
      else
      if (destination.getType() == EDestinationType.cTopic)
      {
        KasqTopic t = mTopicsMap.get(destination.getName());
        if (t == null)
        {
          mLogger.debug("KasqRepository::define() - Topic with name " + destination.getName() + " does not exist. Define it now");
          mTopicsMap.put(destination.getName(), (KasqTopic)destination);
          destination.init();
        }
        else
        {
          mLogger.debug("KasqRepository::define() - Topic with name " + destination.getName() + " already exists");
          success = false;
        }
      }
    }
    
    mLogger.debug("KasqRepository::define() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * Define and initialize a topic/queue in the repository, specifying the name of the topic/queue and the its manager
   * 
   * @param name the name of the topic/queue
   * @param managerName of the manager in which this topic/queue defined
   * @param type the destination type
   * 
   * @return true if topic/queue definition was successful
   */
  private boolean define(String name, String managerName, EDestinationType type)
  {
    mLogger.debug("KasqRepository::define() - IN");
    boolean success = true;
    
    mLogger.info("Define " + type.toString() + " with name=[" + name + "] at manager=[" + managerName + "]");
    
    try
    {
      switch (type)
      {
        case cQueue:
          KasqQueue queue = mQueuesMap.get(name);
          if (queue == null)
          {
            queue = new KasqQueue(name, managerName);
            success = queue.init();
            if (success) mQueuesMap.put(name, queue);
          }
          break;
        case cTopic:
          KasqTopic topic = mTopicsMap.get(name);
          if (topic == null)
          {
            topic = new KasqTopic(name, managerName);
            success = topic.init();
            if (success) mTopicsMap.put(name, topic);
          }
          break;
      }
    }
    catch (Throwable e)
    {
      mLogger.error("Failed to define destination type=[" + type.toString() + "], name=[" + name + "], manager=[" + managerName + "]");
      success = false;
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
    if (name == null)
      return null;
    return mQueuesMap.get(name);
  }

  /***************************************************************************************************************
   * 
   */
  public synchronized KasqTopic locateTopic(String name)
  {
    if (name == null)
      return null;
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
    if (name == null)
      return null;
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
      .append(pad).append("  Queues=(").append(mQueuesMap.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Topics=(").append(mTopicsMap.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
