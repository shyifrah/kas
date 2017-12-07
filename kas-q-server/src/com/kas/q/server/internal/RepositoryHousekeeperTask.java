package com.kas.q.server.internal;

import java.util.Collection;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.server.typedef.DestinationsMap;

public class RepositoryHousekeeperTask extends AKasObject implements Runnable
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(RepositoryHousekeeperTask.class);
  
  /***************************************************************************************************************
   * 
   */
  private DestinationsMap mQueuesMap;
  private DestinationsMap mTopicsMap;
  
  /***************************************************************************************************************
   * Constructs a {@code RepositoryHousekeeperTask} object, passing in the two destinations maps
   * 
   * @param qMap the queues map
   * @param tMap the topics map
   */
  public RepositoryHousekeeperTask(DestinationsMap qMap, DestinationsMap tMap)
  {
    mQueuesMap = qMap;
    mTopicsMap = tMap;
  }
  
  /***************************************************************************************************************
   * Perform {@code KasqRepository} housekeeping.<br>
   * The housekeeping process includes the following tasks:<br>
   * 1. Expire messages<br>
   */
  public void run()
  {
    sLogger.diag("RepositoryHousekeeperTask::run() - IN");
    
    sLogger.debug("RepositoryHousekeeperTask::run() - Repository housekeeping task kicked in...");
    sLogger.debug("RepositoryHousekeeperTask::run() - > Start expiring messages...");
    int total = 0;
    total += expire(mQueuesMap.values());
    total += expire(mTopicsMap.values());
    sLogger.debug("RepositoryHousekeeperTask::run() - > Done expiring. Total expired messages: " + total);
    
    sLogger.diag("RepositoryHousekeeperTask::run() - OUT");
  }
  
  /***************************************************************************************************************
   * Expire messages from a collection of {@code IKasqDestination}. This method is intended to be used with
   * one of the {@code KasqRepository} destination maps: the queues map or the topics map.
   * 
   * @param collection the collection of destinations that should be scanned for expired messages.
   * 
   * @return the number of messages expired in this collection.
   */
  private int expire(Collection<IKasqDestination> collection)
  {
    sLogger.diag("RepositoryHousekeeperTask::expire() - IN");
    
    int total = 0;
    for (IKasqDestination dest : collection)
    {
      sLogger.debug("RepositoryHousekeeperTask::run() - >>> Expiring messages from: " + dest.getName());
      total += dest.expire();
    }
    
    sLogger.diag("RepositoryHousekeeperTask::expire() - OUT");
    return total;
  }

  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
