package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.ServerRepository;

/**
 * The {@link ServerHouseKeeper}, which is a {@link Runnable} object that will be scheduled for execution
 * at a fixed rate to perform some housekeeping tasks.
 * 
 * @author Pippo
 */
public class ServerHouseKeeper extends AKasObject implements Runnable
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Queue repository
   */
  private IRepository mRepository;
  
  /**
   * Stop flag
   */
  private boolean mStop = false;
  
  /**
   * Construct the {@link ServerHouseKeeper}, passing it the {@link IController} and the {@link ServerRepository}
   * 
   * @param repository The server's queue repository
   */
  public ServerHouseKeeper(IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mRepository = repository;
  }
  
  /**
   * Running the task.<br>
   * <br>
   * First we scan all client handlers and check if any of them have finished their executions.
   * If a finished handler is found, remove it from the map.
   * 
   * Secondly, the admin task goes over all defined queues and expires messages which their expiration date has already passed.
   * 
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    mLogger.debug("ServerHouseKeeper::run() - IN");
    
    mLogger.debug("ServerHouseKeeper::run() - Expiring messages...");
    
    Object [] destinations = mRepository.getLocalQueues().toArray();
    for (int i = 0; (i < destinations.length) && (!mStop); ++i)
    {
      MqQueue dest = (MqQueue)destinations[i];
      mLogger.debug("ServerHouseKeeper::run() - Expiring messages in destination " + dest.getName() + ":");
      int exp = dest.expire();
      mLogger.debug("ServerHouseKeeper::run() - Total messages expired: " + exp);
    }
    
    mLogger.debug("ServerHouseKeeper::run() - OUT");
  }
  
  /**
   * Mark the house keeper task it should stop
   */
  public synchronized void stop()
  {
    mStop = true;
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
      .append(pad).append("  Repository=(").append(StringUtils.asPrintableString(mRepository)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
