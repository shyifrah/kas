package com.kas.mq.server.internal;

import java.io.File;
import java.util.Collection;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.typedef.QueueMap;

/**
 * The {@link LocalQueuesManager} is the class that does the actual managing of local queues for the {@link ServerRepository}
 * 
 * @author Pippo
 */
public class LocalQueuesManager extends AKasObject
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
   * The Dead queue
   */
  private MqLocalQueue mDeadQueue;
  
  /**
   * The local KAS/MQ manager
   */
  private MqManager mManager;
  
  /**
   * A map of names to locally defined queues
   */
  private QueueMap mLocalQueues;
  
  /**
   * Construct the {@link LocalQueuesManager}
   * 
   * @param config The {@link MqConfiguration configuration} object
   */
  LocalQueuesManager(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mManager = new MqManager(mConfig.getManagerName(), "localhost", mConfig.getPort());
    mLocalQueues  = new QueueMap();
  }
  
  /**
   * Restore local queues from file system.<br>
   * <br>
   * If the {@code repo} directory does not exist, create it
   * If it exists but it's not a directory, end with an error.
   * Otherwise, read the directory contents and construct a {@link MqLocalQueue} per ".qbk" file.
   * 
   * @return {@code true} if restoration ended successfully, {@code false} otherwise
   */
  boolean restore()
  {
    mLogger.debug("LocalQueuesManager::restore() - IN");
    
    boolean success = true;
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("LocalQueuesManager::restore() - Repository directory does not exist, try to create. result=" + success);
    }
    else if (!repoDir.isDirectory())
    {
      success = false;
      mLogger.fatal("LocalQueuesManager::restore() - Repository directory path points to a file. Terminating...");
    }
    else
    {
      String [] entries = repoDir.list();
      for (String entry : entries)
      {
        if (!entry.endsWith(".qbk"))
          continue;
        
        File qFile = new File(repoDirPath + File.separatorChar + entry);
        if (qFile.isFile())
        {
          String qName = entry.substring(0, entry.lastIndexOf('.'));
          mLogger.trace("LocalQueuesManager::restore() - Restoring contents of queue [" + qName + ']');
          MqLocalQueue q = defineQueue(qName, IMqConstants.cDefaultQueueThreshold);
          boolean restored = q.restore();
          mLogger.trace("LocalQueuesManager::restore() - Restore operation for queue " + qName + (restored ? " succeeded" : " failed"));
          success = success && restored;
        }
      }
      
      mDeadQueue = defineQueue(mConfig.getDeadQueueName(), IMqConstants.cDefaultQueueThreshold, false);
    }
    
    mLogger.debug("LocalQueuesManager::restore() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Backup queues to the file system.<br>
   * <br>
   * For each queue in the map, save its contents as a file in the {@code repo} directory.
   * 
   * @return {@code true} if all queues were successfully backed up, {@code false} otherwise
   */
  boolean backup()
  {
    mLogger.debug("LocalQueuesManager::backup() - IN");
    boolean success = true;
    
    for (MqQueue queue : mLocalQueues.values())
    {
      boolean backed = true;
      String name = queue.getName();
      mLogger.debug("LocalQueuesManager::backup() - Writing queue contents. Queue=[" + name + "]; Messages=[" + queue.size() + "]");
      backed = queue.backup();  
      
      mLogger.debug("LocalQueuesManager::backup() - Writing queue contents completed " + (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mLogger.debug("LocalQueuesManager::backup() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Define a local queue object
   * 
   * @param name The name of the queue to define
   * @param threshold The threshold of the queue
   * @return the created {@link MqLocalQueue}
   */
  MqLocalQueue defineQueue(String name, int threshold)
  {
    return defineQueue(name, threshold, true);
  }
  
  /**
   * Define a local queue object
   * 
   * @param name The name of the queue to define
   * @param threshold The threshold of the queue
   * @param mapped If {@code false}, do not insert the defined queue to the map
   * @return the created {@link MqLocalQueue}
   */
  MqLocalQueue defineQueue(String name, int threshold, boolean mapped)
  {
    mLogger.debug("LocalQueuesManager::defineQueue() - IN, Name=" + name + ", Threshold=" + threshold);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqLocalQueue(mManager, name, threshold);
      if (mapped)
        mLocalQueues.put(name, queue);
    }
    
    mLogger.debug("LocalQueuesManager::defineQueue() - OUT, Returns=" + StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Delete a local queue object
   * 
   * @param name The name of the queue to be removed
   * @return the deleted {@link MqLocalQueue}
   */
  MqLocalQueue deleteQueue(String name)
  {
    mLogger.debug("LocalQueuesManager::deleteQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mLocalQueues.remove(name);
    }
    
    mLogger.debug("LocalQueuesManager::deleteQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Query local queues
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all If {@code true}, display all information on all queues, otherwise, display only names 
   * @return A properties object that holds the queried data
   */
  Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("LocalQueuesManager::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    Properties props = new Properties();
    int total = 0;
    for (MqQueue queue : mLocalQueues.values())
    {
      MqLocalQueue mqlq = (MqLocalQueue)queue;
      boolean include = false;
      if (prefix)
        include = mqlq.getName().startsWith(name);
      else
        include = mqlq.getName().equals(name);
      
      if (include)
      {
        ++total;
        String keyPref = IMqConstants.cKasPropertyQryqResultPrefix + "." + total;
        props.setStringProperty(keyPref + ".name", mqlq.getName());
        if (all)
        {
          props.setStringProperty(keyPref + ".owner", "local");
          props.setIntProperty(keyPref + ".threshold", mqlq.getThreshold());
          props.setIntProperty(keyPref + ".size", mqlq.size());
          props.setStringProperty(keyPref + ".access", mqlq.getLastAccess());
        }
      }
    }
    
    props.setIntProperty(IMqConstants.cKasPropertyQryqResultPrefix + ".total", total);
    mLogger.debug("LocalQueuesManager::queryQueue() - OUT, Returns=" + total + " queues");
    return props;
  }
  
  /**
   * Get a local queue object.
   * 
   * @param name The name of the queue to be retrieved
   * @return the retrieved {@link MqLocalQueue}
   */
  MqLocalQueue getQueue(String name)
  {
    mLogger.debug("LocalQueuesManager::getQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mLocalQueues.get(name);
    }
    
    mLogger.debug("LocalQueuesManager::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return a collection of all local queues
   */
  Collection<MqQueue> getAll()
  {
    return mLocalQueues.values();
  }
  
  /**
   * Get the dead queue
   * 
   * @return the dead queue
   */
  MqLocalQueue getDeadQueue()
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
    sb.append(name()).append("(\n");
    ///
    /// TODO: COMPLETE
    ///
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
