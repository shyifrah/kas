package com.kas.mq.server;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IInitializable;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.MqQueue;

/**
 * The server repository is the class that manages queues
 * 
 * @author Pippo
 */
public class ServerRepository extends AKasObject implements IInitializable
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
   * Admin and Dead queue
   */
  private MqQueue mAdminQueue;
  private MqQueue mDeadQueue;
  
  /**
   * Construct the server repository object.
   * 
   * @param config The {@link MqConfiguration}
   */
  ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mQueueMap = new ConcurrentHashMap<String, MqQueue>();
    mAdminQueue = null;
    mDeadQueue = null;
  }
  
  /**
   * Create a {@link MqQueue} object with the specified {@code name}
   * 
   * @param name The name of the queue
   * @return the {@link MqQueue} object created
   */
  private MqQueue createQueue(String name)
  {
    mLogger.debug("ServerRepository::createQueue() - IN");
    MqQueue queue = null;
    
    name = name.toUpperCase();
    queue = new MqQueue(name);
    mQueueMap.put(name, queue);
    
    mLogger.debug("ServerRepository::createQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get or Create a {@link MqQueue} object with the specified {@code name}.<br>
   * <br>
   * First we try retrieving the queue from the map. If it doesn't exist there, we create it.
   * 
   * @param name The name of the queue
   * @return the {@link MqQueue} object created or retrieved
   */
  private MqQueue getOrCreateQueue(String name)
  {
    mLogger.debug("ServerRepository::getOrCreateQueue() - IN");
    MqQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = mQueueMap.get(name);
      if (queue == null)
        queue = createQueue(name);
    }
    
    mLogger.debug("ServerRepository::getOrCreateQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get the admin queue
   * 
   * @return the admin queue
   */
  public MqQueue getAdminQueue()
  {
    return mAdminQueue;
  }
  
  /**
   * Get the dead queue
   * 
   * @return the dead queue
   */
  public MqQueue getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /**
   * Get queue by name
   * 
   * @return the queue
   */
  public MqQueue getQueue(String name)
  {
    return mQueueMap.get(name);
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
    
    // read repo directory contents and define a queue/topic per backup file found there
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("ServerRepository::init() - Repository directory does not exist, try to create. result=" + success);
    }
    else
    if (!repoDir.isDirectory())
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
        
        File destFile = new File(repoDirPath + File.separatorChar + entry);
        if (destFile.isFile())
        {
          String qName = entry.substring(0, entry.lastIndexOf('.'));
          mLogger.trace("ServerRepository::init() - Restoring contents of queue [" + qName + ']');
          MqQueue q = createQueue(qName);
          q.restore();
        }
      }
      
      mAdminQueue = getOrCreateQueue(mConfig.getAdminQueueName());
      mDeadQueue  = getOrCreateQueue(mConfig.getDeadQueueName());
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
    
    for (MqQueue queue : mQueueMap.values())
    {
      String qname = queue.getName();
      mLogger.debug("ServerRepository::term() - Writing queue contents. Queue=[" + qname + "]; Messages=[" + queue.size() + "]");
      boolean backed = queue.backup();
      mLogger.debug("ServerRepository::term() - Writing queue contents completed " + (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mLogger.debug("ServerRepository::term() - OUT, Returns=" + success);
    return success;
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
