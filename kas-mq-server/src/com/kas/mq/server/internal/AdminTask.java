package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.server.ServerRepository;

/**
 * The {@link AdminTask}, which is a {@link Runnable} object that will be scheduled for execution
 * at a fixed rate to perform some housekeeping tasks.
 * 
 * @author Pippo
 */
public class AdminTask extends AKasObject implements Runnable
{
  private ILogger mLogger;
  private IController mController;
  private ServerRepository mRepository;
  
  /**
   * Construct the {@link AdminTask}, passing it the {@link IController} and the {@link ServerRepository}
   * 
   * @param controller The client controller object
   * @param repository The server's queue repository
   */
  public AdminTask(IController controller, ServerRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mController = controller;
    mRepository = repository;
  }
  
  /**
   * Running the task.<br>
   * <br>
   * First we check the admin queue, which is a predefined queue for receiving administrative requests.
   * If a request is found there, the task will process it and continue to the next one.
   * If there are no administrative requests, the admin task will continue to the second part of its job.
   * 
   * In the second part, the task goes over all defined queues and expires messages which their
   * expiration date has already passed.
   * 
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    mLogger.debug("AdminTask::run() - IN");
    
    MqQueue adminQueue = mRepository.getAdminQueue();
    int adminRequests = adminQueue.size();
    mLogger.trace("Total admin requests in the admin queue: " + adminRequests);
    
    for (int i = 0; i < adminRequests; ++i)
    {
      MqMessage adm = adminQueue.get();
      if (adm != null)
      {
        
      }
    }
    
    // go over all queues defined in the repository
    // find expired messages and remove them
    
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
