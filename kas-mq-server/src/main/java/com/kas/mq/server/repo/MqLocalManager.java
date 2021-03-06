package com.kas.mq.server.repo;

import java.io.File;
import java.util.Collection;
import com.kas.infra.base.IObject;
import com.kas.infra.base.InvalidPropertyValueException;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyNotFoundException;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqQueue;

/**
 * The {@link MqLocalManager} is the class that does the actual managing of local queues for the {@link ServerRepository}
 * 
 * @author Pippo
 */
public class MqLocalManager extends MqManager
{
  /**
   * The Dead queue
   */
  private MqLocalQueue mDeadQueue;
  
  /**
   * Construct the {@link MqLocalManager}
   * 
   * @param name
   *   The name of the local manager
   * @param port
   *   The port on which the manager listens
   * @param deadq
   *   The name of the dead queue
   */
  MqLocalManager(String name, int port, String deadq)
  {
    super(name, "localhost", port);
    mDeadQueue = defineQueue(deadq, "Dead queue", IMqConstants.cDefaultQueueThreshold, EQueueDisp.TEMPORARY);
  }
  
  /**
   * Activate {@link MqLocalManager}: restore local queues from file system.<br>
   * If the {@code repo} directory does not exist, create it
   * If it exists but it's not a directory, end with an error.
   * Otherwise, read the directory contents and construct a {@link MqLocalQueue} per ".qbk" file.
   */
  public void activate()
  {
    mLogger.trace("MqLocalManager::activate() - IN");
    
    boolean success = true;
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("MqLocalManager::activate() - Repository directory does not exist, try to create. result={}", success);
    }
    else if (!repoDir.isDirectory())
    {
      success = false;
      mLogger.fatal("MqLocalManager::activate() - Repository directory path points to a file. Terminating...");
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
          mLogger.debug("MqLocalManager::activate() - Restoring contents of queue [{}]", qName);
          MqLocalQueue q = defineQueue(qName, "", IMqConstants.cDefaultQueueThreshold, EQueueDisp.PERMANENT);
          boolean restored = q.restore();
          mLogger.debug("MqLocalManager::activate() - Restore operation for queue {} {}", qName, (restored ? " succeeded" : " failed"));
          success = success && restored;
        }
      }
    }
    
    mActive = success;
    mLogger.trace("MqLocalManager::activate() - OUT");
  }
  
  /**
   * Deactivate {@link MqLocalManager}: Backup queues to the file system.<br>
   * For each queue in the map, save its contents as a file in the {@code repo} directory.
   */
  public void deactivate()
  {
    mLogger.trace("MqLocalManager::deactivate() - IN");
    boolean success = true;
    
    for (MqQueue queue : mQueues.values())
    {
      MqLocalQueue lq = (MqLocalQueue)queue;
      boolean backed = true;
      String name = queue.getName();
      mLogger.debug("MqLocalManager::deactivate() - Writing queue contents. Queue=[{}]; Messages=[{}]", name, lq.size());
      backed = queue.backup();  
      
      mLogger.debug("MqLocalManager::deactivate() - Writing queue contents completed {}", (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mActive = false;
    mLogger.trace("MqLocalManager::deactivate() - OUT");
  }
  
  /**
   * Define a local queue object
   * 
   * @param name
   *   The name of the queue to define
   * @param desc
   *   The description of the queue
   * @param threshold
   *   The threshold of the queue
   * @param disp
   *   Queue disposition
   * @return
   *   the created {@link MqLocalQueue}
   */
  MqLocalQueue defineQueue(String name, String desc, int threshold, EQueueDisp disp)
  {
    mLogger.trace("MqLocalManager::defineQueue() - IN, Name={}, Threshold={}, Disposition={}", name, threshold, disp);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqLocalQueue(this, name, desc, threshold, disp);
      mQueues.put(name, queue);
    }
    
    mLogger.trace("MqLocalManager::defineQueue() - OUT, Returns={}", StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Alter a local queue object
   * 
   * @param name
   *   The name of the queue to define
   * @param qprops
   *   The properties to be altered
   * @return
   *   the altered {@link MqLocalQueue}
   */
  MqLocalQueue alterQueue(String name, Properties qprops)
  {
    mLogger.trace("MqLocalManager::alterQueue() - IN, Name={}", name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mQueues.get(name);      
        
      try
      {
        if (qprops.containsKey(IMqConstants.cKasPropertyAltThreshold))
        {
          int newThreshold = qprops.getIntProperty(IMqConstants.cKasPropertyAltThreshold);
          queue.setThreshold(newThreshold);
        }
      }
      catch (PropertyNotFoundException e)
      {
        mLogger.error("Unable to set threshold for queue " + name + ". Exception: ", e);    
      }
      catch (InvalidPropertyValueException e)
      {
        mLogger.error("Unable to set threshold for queue " + name + ". Exception: ", e);		
      }
      
      try
      {
        if (qprops.containsKey(IMqConstants.cKasPropertyAltDisp))
        {
          EQueueDisp newDisp = EQueueDisp.fromString(qprops.getStringProperty(IMqConstants.cKasPropertyAltDisp));
          queue.setDisposition(newDisp);
        }
      }

      catch (PropertyNotFoundException e)
      {
        mLogger.error("Unable to set disposition for queue " + name + ". Exception: ", e);    
      }
      catch (InvalidPropertyValueException e)
      {
        mLogger.error("Unable to set disposition for queue " + name + ". Exception: ", e);    
      }
    }
    
    mLogger.trace("MqLocalManager::alterQueue() - OUT, Returns=" + StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Delete a local queue object
   * 
   * @param name
   *   The name of the queue to be removed
   * @return
   *   the deleted {@link MqLocalQueue}
   */
  MqLocalQueue deleteQueue(String name)
  {
    mLogger.trace("MqLocalManager::deleteQueue() - IN, Name={}", name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mQueues.remove(name);
    }
    
    mLogger.trace("MqLocalManager::deleteQueue() - OUT, Returns=[{}]", StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Get a local queue object.
   * 
   * @param name
   *   The name of the queue to be retrieved
   * @return
   *   the retrieved {@link MqLocalQueue}
   */
  MqLocalQueue getQueue(String name)
  {
    mLogger.trace("MqLocalManager::getQueue() - IN, Name={}", name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mQueues.get(name);
    }
    
    mLogger.trace("MqLocalManager::getQueue() - OUT, Returns=[{}]", StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return
   *   a collection of all local queues
   */
  Collection<MqQueue> getAll()
  {
    return mQueues.values();
  }
  
  /**
   * Get the dead queue
   * 
   * @return
   *   the dead queue
   */
  MqLocalQueue getDeadQueue()
  {
    return mDeadQueue;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
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
