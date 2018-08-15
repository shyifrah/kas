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

public class ServerRepository extends AKasObject implements IInitializable
{
  private ILogger mLogger;
  private MqConfiguration mConfig;
  private Map<String, MqQueue> mQueueMap;
  
  ServerRepository(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConfig = config;
    mQueueMap = new ConcurrentHashMap<String, MqQueue>();
  }
  
  private MqQueue createQueue(String name)
  {
    mLogger.debug("ServerRepository::create() - IN");
    MqQueue queue = null;
    
    queue = new MqQueue(name);
    mQueueMap.put(name, queue);
    
    mLogger.debug("ServerRepository::create() - OUT, Returns=[" + StringUtils.asString(queue) + "]");
    return queue;
  }
  
  private MqQueue getOrCreateQueue(String name)
  {
    if (name == null)
      return null;
    
    MqQueue queue = mQueueMap.get(name);
    if (queue == null)
      queue = createQueue(name);
    
    return queue;
  }

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
          createQueue(qName);
        }
      }
      
      getOrCreateQueue(mConfig.getDeadQueueName());
      getOrCreateQueue(mConfig.getAdminQueueName());
    }
    
    mLogger.debug("ServerRepository::init() - OUT, Returns=" + success);
    return success;
  }
  
  public boolean term()
  {
    mLogger.debug("ServerRepository::term() - IN");
    
    for (MqQueue queue : mQueueMap.values())
    {
      String qname = queue.getName();
      mLogger.debug("ServerRepository::term() - Writing queue contents. Queue=[" + qname + "]; Messages=[" + queue.size() + "]");
      queue.term();
    }
    
    mLogger.debug("ServerRepository::term() - OUT");
    return true;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
