package com.kas.mq.server.internal;

import java.io.File;
import java.util.Collection;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqLocalQueue;
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
  private MqLocalQueue mDeadQueue;
  
  /**
   * A map of names to locally defined queues
   */
  private QueueMap mLocalQueues;
  
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
    mLogger.debug("ServerRepository::init() - IN");
    boolean success = true;
    
    mLogger.trace("ServerRepository::init() - Initializing repository...");
    
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("ServerRepository::init() - Repository directory does not exist, try to create. result=" + success);
    }
    else if (!repoDir.isDirectory())
    {
      success = false;
      mLogger.fatal("ServerRepository::init() - Repository directory path points to a file. Terminating...");
    }
    else
    {
      String [] entries = repoDir.list();
      for (String entry : entries)
      {
        // skip files that don't have a "qbk" suffix
        if (!entry.endsWith(".qbk"))
          continue;
        
        File qFile = new File(repoDirPath + File.separatorChar + entry);
        if (qFile.isFile())
        {
          String qName = entry.substring(0, entry.lastIndexOf('.'));
          mLogger.trace("ServerRepository::init() - Restoring contents of queue [" + qName + ']');
          MqLocalQueue q = defineLocalQueue(qName, IMqConstants.cDefaultQueueThreshold);
          q.restore();
        }
      }
      
      if (mLocalQueues.get(mConfig.getDeadQueueName()) == null)
        mDeadQueue = defineLocalQueue(mConfig.getDeadQueueName(), IMqConstants.cDefaultQueueThreshold);
    }
    
    mLogger.debug("ServerRepository::init() - OUT, Returns=" + success);
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
    mLogger.debug("ServerRepository::term() - IN");
    boolean success = true;
    
    for (MqQueue queue : mLocalQueues.values())
    {
      boolean backed = true;
      String name = queue.getName();
      mLogger.debug("ServerRepository::term() - Writing queue contents. Queue=[" + name + "]; Messages=[" + queue.size() + "]");
      backed = queue.backup();  
      
      mLogger.debug("ServerRepository::term() - Writing queue contents completed " + (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mLogger.debug("ServerRepository::term() - OUT, Returns=" + success);
    return success;
  }

  /**
   * Create a {@link MqLocalQueue} object with the specified {@code name} and {@code threshold}.<br>
   * <br>
   * Note that this method does not verify if a queue with that name already exists
   * 
   * @param name The name of the queue
   * @param queue The queue threshold
   * @return the {@link MqLocalQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#createQueue(String)
   */
  public MqLocalQueue defineLocalQueue(String name, int threshold)
  {
    mLogger.debug("ServerRepository::defineLocalQueue() - IN, Name=" + name + ", Threshold=" + threshold);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqLocalQueue(mManager, name, threshold);
      mLocalQueues.put(name, queue);
    }
    
    mLogger.debug("ServerRepository::defineLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Remove a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the queue to be removed
   * @return the {@link MqLocalQueue} object removed
   * 
   * @see com.kas.mq.server.IRepository#deleteLocalQueue(String)
   */
  public MqLocalQueue deleteLocalQueue(String name)
  {
    mLogger.debug("ServerRepository::deleteLocalQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mLocalQueues.remove(name);
    }
    
    mLogger.debug("ServerRepository::deleteLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a {@link MqLocalQueue} object with the specified {@code name}.
   * 
   * @param name The name of the destination to be retrieved
   * @return the {@link MqLocalQueue} object or {@code null} if {@code name} is {@code null}, or there's no queue with this name.
   * 
   * @see IRepository#getLocalQueue(String)
   */
  public MqLocalQueue getLocalQueue(String name)
  {
    mLogger.debug("ServerRepository::getLocalQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mLocalQueues.get(name);
    }
    
    mLogger.debug("ServerRepository::getLocalQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Is a queue exists
   * 
   * @param name The name of the queue to be tested
   * @return {@code true} if a queue named {@code name} exists, {@code false} otherwise 
   * 
   * @see com.kas.mq.server.IRepository#isLocalQueueExist(String)
   */
  public boolean isLocalQueueExist(String name)
  {
    mLogger.debug("ServerRepository::isQueueExist() - IN, Name=" + name);
    
    boolean result = false;
    if ((name != null) && (getLocalQueue(name) != null))
      result = true;
    
    mLogger.debug("ServerRepository::isQueueExist() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get the {@link MqLocalQueue} object representing the dead queue
   * 
   * @return the {@link MqLocalQueue} object of the dead queue
   * 
   * @see com.kas.mq.server.IRepository#getDeadQueue()
   */
  public MqLocalQueue getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /**
   * Get a collection of all queues
   * 
   * @return collection of all queues
   * 
   * @see com.kas.mq.server.IRepository#getLocalQueues()
   */
  public Collection<MqQueue> getLocalQueues()
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
