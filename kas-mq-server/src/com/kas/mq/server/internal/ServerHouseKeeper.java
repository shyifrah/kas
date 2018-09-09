package com.kas.mq.server.internal;

import java.util.Collection;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

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
   * Session controller
   */
  private IController mController;
  
  /**
   * Queue repository
   */
  private IRepository mRepository;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * Construct the {@link ServerHouseKeeper}, passing it the {@link IController} and the {@link ServerRepository}
   * 
   * @param controller The client controller object
   * @param repository The server's queue repository
   */
  public ServerHouseKeeper(IController controller, IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mController = controller;
    mRepository = repository;
    mConfig = controller.getConfig();
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
    
    if (!mConfig.isEnabled())
    {
      mLogger.debug("ServerHouseKeeper::run() - KAS/MQ Server is disabled");
    }
    else if (!mConfig.isHousekeeperEnabled())
    {
      mLogger.debug("ServerHouseKeeper::run() - KAS/MQ Server housekeeper is disabled");
    }
    else
    {
      mLogger.debug("ServerHouseKeeper::run() - Perform handlers cleanup...");
      Map<UniqueId, SessionHandler> handlers = mController.getHandlers();
      for (Map.Entry<UniqueId, SessionHandler> entry : handlers.entrySet())
      {
        UniqueId uid = entry.getKey();
        SessionHandler handler = entry.getValue();
        
        mLogger.diag("ServerHouseKeeper::run() - Checking handler ID: " + uid);
        if (handler.isRunning())
        {
          mLogger.diag("ServerHouseKeeper::run() - Handler is still running, skip it");
        }
        else
        {
          mLogger.debug("ServerHouseKeeper::run() - Handler " + uid + " finished working. Remove from the map");
          handlers.remove(uid);
          ThreadPool.removeTask(handler);
        }
      }
      
      mLogger.debug("ServerHouseKeeper::run() - Expiring messages...");
      Collection<MqQueue> destinations = mRepository.getLocalQueues();
      for (MqQueue dest : destinations)
      {
        mLogger.debug("ServerHouseKeeper::run() - Expiring messages in destination " + dest.getName() + ":");
        mLogger.diag("ServerHouseKeeper::run() - Total messages in destination: " + dest.size());
        int exp = dest.expire();
        mLogger.diag("ServerHouseKeeper::run() - Total messages in destination: " + dest.size());
        mLogger.debug("ServerHouseKeeper::run() - Total messages expired: " + exp);
      }
    }
    
    mLogger.debug("ServerHouseKeeper::run() - OUT");
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
      .append(pad).append("  Controller=(").append(StringUtils.asPrintableString(mController)).append(")\n")
      .append(pad).append("  Repository=(").append(StringUtils.asPrintableString(mRepository)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
