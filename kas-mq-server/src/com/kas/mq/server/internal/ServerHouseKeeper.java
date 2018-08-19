package com.kas.mq.server.internal;

import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.ServerRepository;

/**
 * The {@link ServerHouseKeeper}, which is a {@link Runnable} object that will be scheduled for execution
 * at a fixed rate to perform some housekeeping tasks.
 * 
 * @author Pippo
 */
public class ServerHouseKeeper extends AKasObject implements Runnable
{
  private ILogger mLogger;
  private IController mController;
  private ServerRepository mRepository;
  private MqConfiguration mConfig;
  
  /**
   * Construct the {@link ServerHouseKeeper}, passing it the {@link IController} and the {@link ServerRepository}
   * 
   * @param controller The client controller object
   * @param repository The server's queue repository
   */
  public ServerHouseKeeper(IController controller, ServerRepository repository)
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
    mLogger.debug("AdminTask::run() - IN");
    
    if (mConfig.isHousekeeperEnabled())
    {
      mLogger.debug("AdminTask::run() - Perform handlers cleanup...");
      Map<UniqueId, SessionHandler> handlers = mController.getHandlers();
      for (Map.Entry<UniqueId, SessionHandler> entry : handlers.entrySet())
      {
        UniqueId uid = entry.getKey();
        SessionHandler handler = entry.getValue();
        
        mLogger.diag("AdminTask::run() - Checking handler ID: " + uid);
        if (handler.isRunning())
        {
          mLogger.diag("AdminTask::run() - Handler is still running, skip it");
        }
        else
        {
          mLogger.debug("AdminTask::run() - Handler " + uid + " finished working. Remove from the map");
          handlers.remove(uid);
          ThreadPool.removeTask(handler);
        }
      }
    }
    
    mLogger.debug("AdminTask::run() - OUT");
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
