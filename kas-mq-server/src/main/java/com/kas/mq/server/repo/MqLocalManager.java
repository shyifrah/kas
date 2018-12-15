package com.kas.mq.server.repo;

import java.io.File;
import java.util.Collection;

import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
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
   * @param name The name of the local manager
   * @param port The port on which the manager listens
   * @param deadq The name of the dead queue
   */
  MqLocalManager(String name, int port, String deadq)
  {
    super(name, "localhost", port);
    mDeadQueue = defineQueue(deadq, IMqConstants.cDefaultQueueThreshold, false);
  }
  
  /**
   * Activate {@link MqLocalManager}: restore local queues from file system.<br>
   * <br>
   * If the {@code repo} directory does not exist, create it
   * If it exists but it's not a directory, end with an error.
   * Otherwise, read the directory contents and construct a {@link MqLocalQueue} per ".qbk" file.
   */
  public void activate()
  {
    mLogger.debug("MqLocalManager::activate() - IN");
    
    boolean success = true;
    String repoDirPath = RunTimeUtils.getProductHomeDir() + File.separatorChar + "repo";
    File repoDir = new File(repoDirPath);
    if (!repoDir.exists())
    {
      success = repoDir.mkdir();
      mLogger.info("MqLocalManager::activate() - Repository directory does not exist, try to create. result=" + success);
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
          mLogger.trace("MqLocalManager::activate() - Restoring contents of queue [" + qName + ']');
          MqLocalQueue q = defineQueue(qName, IMqConstants.cDefaultQueueThreshold);
          boolean restored = q.restore();
          mLogger.trace("MqLocalManager::activate() - Restore operation for queue " + qName + (restored ? " succeeded" : " failed"));
          success = success && restored;
        }
      }
    }
    
    mActive = success;
    mLogger.debug("MqLocalManager::activate() - OUT");
  }
  
  /**
   * Deactivate {@link MqLocalManager}: Backup queues to the file system.<br>
   * <br>
   * For each queue in the map, save its contents as a file in the {@code repo} directory.
   */
  public void deactivate()
  {
    mLogger.debug("MqLocalManager::deactivate() - IN");
    boolean success = true;
    
    for (MqQueue queue : mQueues.values())
    {
      MqLocalQueue lq = (MqLocalQueue)queue;
      boolean backed = true;
      String name = queue.getName();
      mLogger.debug("MqLocalManager::deactivate() - Writing queue contents. Queue=[" + name + "]; Messages=[" + lq.size() + "]");
      backed = queue.backup();  
      
      mLogger.debug("MqLocalManager::deactivate() - Writing queue contents completed " + (success ? "successfully" : "with errors"));
      success = success && backed;
    }
    
    mActive = false;
    mLogger.debug("MqLocalManager::deactivate() - OUT");
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
   * @param backup Should the queue be backed up to file-system
   * @return the created {@link MqLocalQueue}
   */
  MqLocalQueue defineQueue(String name, int threshold, boolean backup)
  {
    mLogger.debug("MqLocalManager::defineQueue() - IN, Name=" + name + ", Threshold=" + threshold + ", BackupToFilesys=" + backup);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = new MqLocalQueue(this, name, threshold, backup);
      mQueues.put(name, queue);
    }
    
    mLogger.debug("MqLocalManager::defineQueue() - OUT, Returns=" + StringUtils.asString(queue));
    return queue;
  }
  
  /**
   * Alter a local queue object
   * 
   * @param name The name of the queue to define
   * @param qprops The properties to be altered
   * @return the altered {@link MqLocalQueue}
   */
  MqLocalQueue alterQueue(String name, Properties qprops)
  {
    mLogger.debug("MqLocalManager::alterQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue) mQueues.get(name);      
        
      try {
    	  if ( qprops.containsKey(IMqConstants.cKasPropertyAltThreshold) )
    	  {
    		  queue.setThreshold(qprops.getIntProperty(IMqConstants.cKasPropertyAltThreshold));
    	  }
      } catch (KasException e) {			
    	  mLogger.error("Unable to set Threshold for current queue" + e.getMessage());		
      }
      
      try {
    	  if ( qprops.containsKey(IMqConstants.cKasPropertyAltPermanent) )
    	  {
    		  queue.setPermanentValue(qprops.getBoolProperty(IMqConstants.cKasPropertyAltPermanent));
    	  }
      } catch (KasException e) {			
    	  mLogger.error("Unable to set Permanent value for current queue" + e.getMessage());		
      }    
      mQueues.remove(name);
      mQueues.put(name, queue);      
    }    
    mLogger.debug("MqLocalManager::alterQueue() - OUT, Returns=" + StringUtils.asString(queue));
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
    mLogger.debug("MqLocalManager::deleteQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mQueues.remove(name);
    }
    
    mLogger.debug("MqLocalManager::deleteQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
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
    mLogger.debug("MqLocalManager::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    Properties props = new Properties();
    for (MqQueue queue : mQueues.values())
    {
      MqLocalQueue mqlq = (MqLocalQueue)queue;
      boolean include = false;
      if (prefix)
        include = mqlq.getName().startsWith(name);
      else
        include = mqlq.getName().equals(name);
      
      mLogger.debug("MqLocalManager::queryQueue() - Checking if current queue [" + mqlq.getName() + "] matches query: " + include);
      if (include)
      {
        String key = IMqConstants.cKasPropertyQueryResultPrefix + "." + mqlq.getName();
        props.setStringProperty(key, mqlq.queryResponse(all));
      }
    }
    mLogger.debug("MqLocalManager::queryQueue() - OUT, Returns=" + props.size() + " queues");
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
    mLogger.debug("MqLocalManager::getQueue() - IN, Name=" + name);
    MqLocalQueue queue = null;
    
    if (name != null)
    {
      name = name.toUpperCase();
      queue = (MqLocalQueue)mQueues.get(name);
    }
    
    mLogger.debug("MqLocalManager::getQueue() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  /**
   * Get a collection of all local queues
   * 
   * @return a collection of all local queues
   */
  Collection<MqQueue> getAll()
  {
    return mQueues.values();
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
