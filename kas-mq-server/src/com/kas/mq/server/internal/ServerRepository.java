package com.kas.mq.server.internal;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.typedef.QueueMap;

/**
 * The server repository is the class that manages queues
 * 
 * @author Pippo
 */
public class ServerRepository extends AKasObject implements IRepository
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
   * The local KAS/MQ manager
   */
  private MqManager mManager;
  
  /**
   * The dead queue
   */
  private MqQueue mDeadQueue;
  
  /**
   * A map of names to locally defined queues
   */
  private QueueMap mLocalQueues;
  
  /**
   * A map of names to remotely defined queues
   */
  private QueueMap mRemoteQueues;
  
  /**
   * A map of names to remote managers
   */
  private Map<String, MqManager> mRemoteManagers;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  public ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mManager = new MqManager(mConfig.getManagerName(), "localhost", mConfig.getPort());
    mLocalQueues  = new QueueMap();
    mRemoteQueues = new QueueMap();
    mRemoteManagers = new ConcurrentHashMap<String, MqManager>();
  }
  
  /**
   * Initialize the server repository
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public boolean init()
  {
    mLogger.debug("QueueRepository::init() - IN");
    boolean success = true;
    
    mLogger.trace("QueueRepository::init() - Initializing repository...");
    
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("QueueRepository::init() - Repository directory does not exist, try to create. result=" + success);
    }
    else if (!repoDir.isDirectory())
    {
      success = false;
      mLogger.fatal("QueueRepository::init() - Repository directory path points to a file. Terminating...");
    }
    else
    {
      String [] entries = repoDir.list();
      for (String entry : entries)
      {
        // skip files that don't have a "qbk" suffix
        if (!entry.endsWith(".qbk"))
          continue;
        
        File destFile = new File(repoDirPath + File.separatorChar + entry);
        if (destFile.isFile())
        {
          String qName = entry.substring(0, entry.lastIndexOf('.'));
          mLogger.trace("QueueRepository::init() - Restoring contents of queue [" + qName + ']');
          MqQueue q = createQueue(qName, IMqConstants.cDefaultQueueThreshold);
          q.restore();
        }
      }
      
      if (mLocalQueues.get(mConfig.getDeadQueueName()) == null)
        mDeadQueue = createQueue(mConfig.getDeadQueueName(), IMqConstants.cDefaultQueueThreshold);
      
      Map<String, NetworkAddress> remoteManagersMap = mConfig.getRemoteManagers();
      for (Map.Entry<String, NetworkAddress> entry : remoteManagersMap.entrySet())
      {
        String name = entry.getKey();
        NetworkAddress addr = entry.getValue();
        MqManager mgr = new MqManager(name, addr.getHost(), addr.getPort());
        mRemoteManagers.put(name, mgr);
      }
    }
    
    mLogger.debug("QueueRepository::init() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Terminate the server repository
   * 
   * @return {@code true} if termination completed successfully, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public boolean term()
  {
    mLogger.debug("QueueRepository::term() - IN");
    boolean success = true;
    
    for (MqQueue queue : mLocalQueues.values())
    {
      String qname = queue.getName();
      mLogger.debug("QueueRepository::term() - Writing queue contents. Queue=[" + qname + "]; Messages=[" + queue.size() + "]");
      boolean backed = queue.backup();
      mLogger.debug("QueueRepository::term() - Writing queue contents completed " + (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mLogger.debug("QueueRepository::term() - OUT, Returns=" + success);
    return success;
  }

  /**
   * Create a {@link MqQueue} object with the specified {@code name} and {@code threshold}.<br>
   * <br>
   * Note that this method does not verify if a queue with that name already exists
   * 
   * @param name The name of the queue
   * @param queue The queue threshold
   * @return the {@link MqQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#createQueue(String)
   */
  public MqQueue createQueue(String name, int threshold)
  {
    mLogger.debug("QueueRepository::createQueue() - IN, Name=" + name + ", Threshold=" + threshold);
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqQueue(mManager, name, threshold);
      mLocalQueues.put(name, queue);
    }
    
    mLogger.debug("QueueRepository::createQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Remove a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqQueue} object removed
   * 
   * @see com.kas.mq.server.IRepository#removeQueue(String)
   */
  public MqQueue removeQueue(String name)
  {
    mLogger.debug("QueueRepository::removeQueue() - IN, Name=" + name);
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mLocalQueues.remove(name);
    }
    
    mLogger.debug("QueueRepository::removeQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a {@link MqQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be retrieved
   * @return the {@link MqQueue} object or {@code null}, if {@code name} is {@code null}, or there's no queue with this name.
   * 
   * @see com.kas.mq.server.IRepository#getQueue(String)
   */
  public MqQueue getQueue(String name)
  {
    mLogger.debug("QueueRepository::getQueue() - IN, Name=" + name);
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mLocalQueues.get(name);
      if (queue == null)
        queue = mRemoteQueues.get(name);
    }
    
    mLogger.debug("QueueRepository::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Is a queue exists
   * 
   * @param name The name of the queue to be tested
   * @return {@code true} if a queue named {@code name} exists, {@code false} otherwise 
   * 
   * @see com.kas.mq.server.IRepository#isQueueExist(String)
   */
  public boolean isQueueExist(String name)
  {
    mLogger.debug("QueueRepository::isQueueExist() - IN, Name=" + name);
    
    boolean result = false;
    if ((name != null) && (getQueue(name) != null))
      result = true;
    
    mLogger.debug("QueueRepository::isQueueExist() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get the {@link MqQueue} object representing the dead queue
   * 
   * @return the {@link MqQueue} object of the dead queue
   * 
   * @see com.kas.mq.server.IRepository#getDeadQueue()
   */
  public MqQueue getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /**
   * Get a collection of all queues
   * 
   * @return collection of all queues
   * 
   * @see com.kas.mq.server.IRepository#getElements()
   */
  public Collection<MqQueue> getElements()
  {
    return mLocalQueues.values();
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
