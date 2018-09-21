package com.kas.mq.server.processors;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqConnection;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

/**
 * Processor for defining queues
 * 
 * @author Pippo
 */
public class DefineQueueProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * queue name and quue threshold
   */
  private String mQueue;
  private int mThreshold;
  
  /**
   * Construct a {@link DefineQueueProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  DefineQueueProcessor(IMqMessage<?> request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process queue definition request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("DefineQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DefineQueueProcessor::process() - " + mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyDefqQueueName, null);
      mThreshold = mRequest.getIntProperty(IMqConstants.cKasPropertyDefqThreshold, IMqConstants.cDefaultQueueThreshold);
      mLogger.debug("DefineQueueProcessor::process() - Queue=" + mQueue + "; Threshold=" + mThreshold);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      
      if (mqlq != null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" already exists";
        mLogger.debug("DefineQueueProcessor::process() - " + mDesc);
      }
      else
      {
        mqlq = mRepository.defineLocalQueue(mQueue, mThreshold);
        mLogger.debug("DefineQueueProcessor::process() - Created queue " + StringUtils.asPrintableString(mqlq));
        mDesc = "Queue with name " + mQueue + " and threshold of " + mThreshold + " was successfully defined";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.debug("DefineQueueProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process queue definition request.<br>
   * <br>
   * If queue definition was successful, we need to inform remote KAS/MQ managers
   * that the local repository was updated and that they should update their repository
   * to reflect these changes.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} in case the handler should continue process next request,
   * {@code false} if it should terminate 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage, IMqMessage)
   */
  public boolean postprocess(IMqMessage<?> reply)
  {
    mLogger.debug("DefineQueueProcessor::postprocess() - IN");
    
    if (mCode == EMqCode.cOkay)
    {
      Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
      String localQmgr = mConfig.getManagerName();
      
      for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
      {
        String remoteQmgrName  = entry.getKey();
        NetworkAddress address = entry.getValue();
        
        mLogger.debug("DefineQueueProcessor::postprocess() - Notifying KAS/MQ server \"" + remoteQmgrName + "\" (" + address.toString() + ") on repository update");
        
        MqConnection client = new MqConnection();
        client.connect(address.getHost(), address.getPort());
        if (client.isConnected())
        {
          boolean success = client.notifyRepoUpdate(localQmgr, mQueue, true);
          mLogger.debug("DefineQueueProcessor::postprocess() - Notification returned: " + success);
          
          client.disconnect();
        }
      }
    }
    
    mLogger.debug("DefineQueueProcessor::postprocess() - OUT");
    return true;
  }
}
