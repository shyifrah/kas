package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for putting a message into a queue
 * 
 * @author Pippo
 */
public class PutMessageProcessor extends AProcessor
{
  /**
   * The session's handler
   */
  private SessionHandler mHandler;
  
  /**
   * Extracted input from the request:
   * Queue name to which message should be put 
   */
  private String mQueue;
  
  /**
   * Construct a {@link PutMessageProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param handler The session handler
   */
  PutMessageProcessor(IMqMessage<?> request, IController controller, SessionHandler handler)
  {
    super(request, controller);
    mHandler = handler;
  }
  
  /**
   * Process put message request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("PutMessageProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mLogger.debug("GetMessageProcessor::process() - KAS/MQ server is disabled");
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyPutQueueName, null);
      mLogger.debug("PutMessageProcessor::process() - Queue=" + mQueue);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      MqLocalQueue dead = mRepository.getDeadQueue();
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mLogger.debug("PutMessageProcessor::process() - Invalid queue name: null or empty string");
        dead.put(mRequest);
      }
      else if (mqlq == null)
      {
        mLogger.debug("PutMessageProcessor::process() - Queue with name \"" + mQueue + "\" doesn't exist");
        dead.put(mRequest);
      }
      else
      {
        boolean success = mqlq.put(mRequest);
        if (!success) dead.put(mRequest);
        
        mLogger.debug("PutMessageProcessor::process() - Message was put to queue " + mQueue);
        mqlq.setLastAccess(mHandler.getActiveUserName(), "put");
      }
    }
    
    mLogger.debug("PutMessageProcessor::process() - OUT");
    return null;
  }
}
