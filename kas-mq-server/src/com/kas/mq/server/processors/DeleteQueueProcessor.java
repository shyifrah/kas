package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IController;

/**
 * Processor for deleting queues
 * 
 * @author Pippo
 */
public class DeleteQueueProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * the queue name and if deletion should be forced
   */
  private String mQueue;
  private boolean mForce;
  
  /**
   * Construct a {@link DeleteQueueProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   */
  DeleteQueueProcessor(IMqMessage<?> request, IController controller)
  {
    super(request, controller);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("DeleteQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyDelqQueueName, null);
      mForce = mRequest.getBoolProperty(IMqConstants.cKasPropertyDelqForce, false);
      mLogger.debug("DeleteQueueProcessor::process() - Queue=" + mQueue + "; Force=" + mForce);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      
      if (mqlq == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else if (mqlq.size() == 0)
      {
        mRepository.deleteLocalQueue(mQueue);
        mDesc = "Queue with name \"" + mQueue + "\" was successfully deleted";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else if (mForce)
      {
        int size = mqlq.size();
        mRepository.deleteLocalQueue(mQueue);
        mDesc = "Queue with name \"" + mQueue + "\" was successfully deleted (" + size + " messages discarded)";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else
      {
        int size = mqlq.size();
        mDesc = "Queue is not empty (" + size + " messages) and FORCE was not specified";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
    }
    
    mLogger.debug("DeleteQueueProcessor::process() - OUT");
    return respond();
  }
}
