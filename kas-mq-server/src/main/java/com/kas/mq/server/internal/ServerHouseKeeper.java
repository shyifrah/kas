package com.kas.mq.server.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.IObject;
import com.kas.infra.base.threads.AKasRunnable;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.ServerRepository;

/**
 * The {@link ServerHouseKeeper} is a {@link Runnable} object that will be
 * scheduled for execution at a fixed rate to perform some housekeeping tasks.<br>
 * <br>
 * Housekeeping tasks include:<br>
 * 1. Expiring old messages
 * 
 * @author Pippo
 */
public class ServerHouseKeeper extends AKasRunnable
{
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * Queue repository
   */
  private IRepository mRepository;
  
  /**
   * Construct the {@link ServerHouseKeeper}, passing it the {@link IController}
   * and the {@link ServerRepository}
   * 
   * @param repository
   *   The server's queue repository
   */
  public ServerHouseKeeper(IRepository repository)
  {
    mLogger = LogManager.getLogger(getClass());
    mRepository = repository;
  }
  
  /**
   * Running the task.<br>
   * First we scan all client handlers and check if any of them have finished their executions.
   * If a finished handler is found, remove it from the map.<br>
   * Secondly, the admin task goes over all defined queues and expires messages which
   * their expiration date has already passed.
   * 
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    mLogger.trace("ServerHouseKeeper::run() - IN");
    
    mLogger.trace("ServerHouseKeeper::run() - Expiring messages...");
    
    Object [] destinations = mRepository.getLocalQueues().toArray();
    for (int i = 0; (i < destinations.length) && (!mStop); ++i)
    {
      MqQueue dest = (MqQueue)destinations[i];
      mLogger.trace("ServerHouseKeeper::run() - Expiring messages in destination {}:", dest.getName());
      int exp = dest.expire();
      mLogger.trace("ServerHouseKeeper::run() - Total messages expired: {}", exp);
    }
    
    mLogger.trace("ServerHouseKeeper::run() - OUT");
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
    sb.append(name()).append("(\n")
      .append(pad).append("  Repository=(").append(StringUtils.asPrintableString(mRepository)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
