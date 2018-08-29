package com.kas.mq.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.server.IRepository;

/**
 * The server repository is the class that manages queues
 * 
 * @author Pippo
 */
public class QueueRepository extends AKasObject implements IRepository
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
   * A map of all defined queues
   */
  private Map<String, MqQueue> mQueueMap;
  
  /**
   * Dead queue name
   */
  private MqQueue mDeadQueue;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  public QueueRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mQueueMap = new ConcurrentHashMap<String, MqQueue>();
    mDeadQueue = null;
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
    
    // read repo directory contents and define a queue per backup file found there
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("QueueRepository::init() - Repository directory does not exist, try to create. result=" + success);
    }
    else
    if (!repoDir.isDirectory())
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
          MqQueue q = getOrCreateQueue(qName);
          q.restore();
        }
      }
      
      mDeadQueue  = getOrCreateQueue(mConfig.getDeadQueueName());
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
    
    for (MqQueue queue : mQueueMap.values())
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
   * Create a {@link MqQueue} object with the specified {@code name}.<br>
   * <br>
   * Note that this method does not verify if a queue with that name already exists
   * 
   * @param name The name of the queue
   * @return the {@link MqQueue} object created
   * 
   * @see com.kas.mq.server.IRepository#createQueue(String)
   */
  public MqQueue createQueue(String name)
  {
    mLogger.debug("QueueRepository::createQueue() - IN");
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqQueue(name);
      mQueueMap.put(name, queue);
    }
    
    mLogger.debug("QueueRepository::createQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get or Create a {@link MqQueue} object with the specified {@code name}.<br>
   * <br>
   * First we try retrieving the queue from the map. If it doesn't exist there, we create it.
   * 
   * @param name The name of the queue
   * @return the {@link MqQueue} object created or retrieved
   * 
   * @see com.kas.mq.server.IRepository#getOrCreateQueue(String)
   */
  public MqQueue getOrCreateQueue(String name)
  {
    mLogger.debug("QueueRepository::getOrCreateQueue() - IN");
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mQueueMap.get(name);
      if (queue == null)
        queue = createQueue(name);
    }
    
    mLogger.debug("QueueRepository::getOrCreateQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
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
    mLogger.debug("QueueRepository::removeQueue() - IN");
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mQueueMap.remove(name);
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
    mLogger.debug("QueueRepository::getQueue() - IN");
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mQueueMap.get(name);
    }
    
    mLogger.debug("QueueRepository::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
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
    sb.append(name()).append("(\n")
      .append(pad).append("  Queue Map=(").append(StringUtils.asPrintableString(mQueueMap, level+2)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
