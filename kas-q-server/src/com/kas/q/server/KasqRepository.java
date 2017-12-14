package com.kas.q.server;

import java.io.File;
import java.util.concurrent.TimeUnit;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.server.internal.MessagingConfiguration;
import com.kas.q.server.internal.RepositoryHousekeeperTask;
import com.kas.q.server.typedef.DestinationsMap;

public class KasqRepository extends AKasObject implements IInitializable
{
  /***************************************************************************************************************
   * 
   */
  private ILogger mLogger;
  private MessagingConfiguration mConfig;
  
  private DestinationsMap mQueuesMap;
  private DestinationsMap mTopicsMap;
  
  private IKasqDestination mDeadQueue;
  private IKasqDestination mAdminQueue;
  
  private RepositoryHousekeeperTask mHouseKeeper;
  
  /***************************************************************************************************************
   * Construct the repository, specifying the Messaging configuration object.
   * 
   * @param config the {@code MessagingConfiguration} object
   */
  KasqRepository()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = KasqServer.getInstance().getConfiguration();
    
    mQueuesMap = new DestinationsMap();
    mTopicsMap = new DestinationsMap();
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
          define(destType, destName, mConfig.getManagerName());
        }
      }
      
      IKasqDestination q;
      q = defineQueueIfNeeded(mConfig.getDeadQueue());
      mDeadQueue = q;
      
      q = defineQueueIfNeeded(mConfig.getAdminQueue());
      mAdminQueue = q;
      
      mHouseKeeper = new RepositoryHousekeeperTask(mQueuesMap, mTopicsMap);
      ThreadPool.scheduleAtFixedRate(mHouseKeeper, mConfig.getHouseKeeperDelay(), mConfig.getHouseKeeperInterval(), TimeUnit.MINUTES);
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
    
    ThreadPool.removeSchedule(mHouseKeeper);
    
    for (IKasqDestination queue : mQueuesMap.values())
    {
      String qname = "unknown-queue";
      try
      {
        qname = queue.getName();
      }
      catch (Throwable e) {}
      mLogger.debug("KasqRepository::term() - Writing queue contents. Queue=[" + qname + "]; Messages=[" + queue.size() + "]");
      queue.term();
    }
    
    for (IKasqDestination topic : mTopicsMap.values())
    {
      String tname = "unknown-topic";
      try
      {
        tname = topic.getName();
      }
      catch (Throwable e) {}
      mLogger.debug("KasqRepository::term() - Writing topic contents. Topic=[" + tname + "]; Messages=[" + topic.size() + "]");
      topic.term();
    }
    
    mHouseKeeper = null;
    
    mLogger.debug("KasqRepository::term() - OUT");
    return true;
  }
  
  /***************************************************************************************************************
   * Define and initialize a queue only if needed.
   * 
   * @param name the name of the queue
   * 
   * @return true if queue definition was successful
   */
  private IKasqDestination defineQueueIfNeeded(String name)
  {
    IKasqDestination queue = locateQueue(name);
    if (queue == null)
    {
      mLogger.trace("KasqRepository::init() - Queue [" + name + "] could not be located, define it now");
      queue = define(EDestinationType.cQueue, name, mConfig.getManagerName());
    }
    
    return queue;
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
        IKasqDestination q = mQueuesMap.get(destination.getName());
        if (q == null)
        {
          mLogger.debug("KasqRepository::define() - Queue with name " + destination.getName() + " does not exist. Define it now");
          mQueuesMap.put(destination.getName(), destination);
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
        IKasqDestination t = mTopicsMap.get(destination.getName());
        if (t == null)
        {
          mLogger.debug("KasqRepository::define() - Topic with name " + destination.getName() + " does not exist. Define it now");
          mTopicsMap.put(destination.getName(), destination);
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
   * Define and initialize a topic/queue in the repository, specifying the name of the topic/queue
   * and the its manager
   * 
   * @param name the name of the topic/queue
   * @param managerName of the manager in which this topic/queue defined
   * @param type the destination type
   * 
   * @return the newly-defined destination or null if definition failed
   */
  private IKasqDestination define(EDestinationType type, String name, String managerName)
  {
    mLogger.debug("KasqRepository::define() - IN");
    IKasqDestination destination = null;
    
    mLogger.info("Define " + type.toString() + " with name=[" + name + "] at manager=[" + managerName + "]");
    boolean success;
    
    try
    {
      switch (type)
      {
        case cQueue:
          destination = new KasqQueue(name, managerName);
          success = destination.init();
          if (success) mQueuesMap.put(name, destination);
          break;
        case cTopic:
          destination = new KasqTopic(name, managerName);
          success = destination.init();
          if (success) mTopicsMap.put(name, destination);
          break;
        default:
          break;
      }
    }
    catch (Throwable e)
    {
      mLogger.error("Failed to define destination type=[" + type.toString() + "], name=[" + name + "], manager=[" + managerName + "]");
      success = false;
    }
    
    mLogger.debug("KasqRepository::define() - OUT, Returns=" + (destination != null));
    return destination;
  }
  
  /***************************************************************************************************************
   * Gets the dead queue
   * 
   * @return the {@code KasqQueue} object of the dead queue
   */
  public IKasqDestination getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /***************************************************************************************************************
   * Gets the admin queue
   * 
   * @return the {@code KasqQueue} object of the admin queue
   */
  public IKasqDestination getAdminQueue()
  {
    return mAdminQueue;
  }
  
  /***************************************************************************************************************
   * Locate a {@code KasqQueue} based on its name.
   * 
   * @param name the name of the queue
   * 
   * @return the {@code KasqQueue} associated with the name, or {@code null} if it could not be found
   */
  public synchronized IKasqDestination locateQueue(String name)
  {
    if (name == null)
      return null;
    return mQueuesMap.get(name);
  }

  /***************************************************************************************************************
   * Locate a {@code KasqTopic} based on its name.
   * 
   * @param name the name of the topic
   * 
   * @return the {@code KasqTopic} associated with the name, or {@code null} if it could not be found
   */
  public synchronized IKasqDestination locateTopic(String name)
  {
    if (name == null)
      return null;
    return mTopicsMap.get(name);
  }
  
  /***************************************************************************************************************
   * Locate a {@code IKasqDestination} object based on it's name.<br>
   * First we look in the queues map, then in the topics map.
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
   * Query information
   * 
   * @param type the destination type
   * @param name the destination name
   * 
   * @return the queried information in textual format
   */
  public String query(EDestinationType type, String name)
  {
    StringBuffer sb = new StringBuffer();
    if (type == EDestinationType.cAll)   // QUERY ALL
    {
      for (IKasqDestination queue : mQueuesMap.values())
        sb.append(queue.toPrintableString(0))
          .append("\n--------------------------------------------------------------------------\n");
      for (IKasqDestination topic : mTopicsMap.values())
        sb.append(topic.toPrintableString(0))
          .append("\n--------------------------------------------------------------------------\n");
    }
    else                                 // QUERY TOPIC ALL
    if ((type == EDestinationType.cTopic) && (name == null))
    {
      for (IKasqDestination topic : mTopicsMap.values())
        sb.append(topic.toPrintableString(0))
          .append("\n--------------------------------------------------------------------------\n");
    }
    else                                 // QUERY QUEUE ALL
    if ((type == EDestinationType.cQueue) && (name == null))
    {
      for (IKasqDestination queue : mQueuesMap.values())
        sb.append(queue.toPrintableString(0))
          .append("\n--------------------------------------------------------------------------\n");
    }
    else                                 // QUERY TOPIC name
    if (type == EDestinationType.cTopic)
    {
      IKasqDestination topic = locateTopic(name);
      sb.append(topic.toPrintableString(0))
        .append("\n--------------------------------------------------------------------------\n");
    }
    else                                 // QUERY QUEUE name
    if (type == EDestinationType.cQueue)
    {
      IKasqDestination queue = locateQueue(name);
      sb.append(queue.toPrintableString(0))
        .append("\n--------------------------------------------------------------------------\n");
    }
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  AdminQueue=(").append(mAdminQueue.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  DeadQueue=(").append(mDeadQueue.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Queues=(\n")
      .append(StringUtils.asPrintableString(mQueuesMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  Topics=(\n")
      .append(StringUtils.asPrintableString(mTopicsMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
