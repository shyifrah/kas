package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IController;

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
   */
  DefineQueueProcessor(IMqMessage<?> request, IController controller)
  {
    super(request, controller);
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
}
