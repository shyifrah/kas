package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.server.IController;

/**
 * Processor for putting a message into a queue
 * 
 * @author Pippo
 */
public class MessagePutProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * Queue name to which message should be put 
   */
  private String mQueue;
  
  /**
   * Construct a {@link MessagePutProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param handler The session handler
   */
  MessagePutProcessor(IMqMessage<?> request, IController controller)
  {
    super(request, controller);
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
      
      
      MqQueue queue = mRepository.getQueue(mQueue);
      MqLocalQueue dead = mRepository.getDeadQueue();
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mDesc = "Invalid queue name: null or empty string";
        mLogger.debug("PutMessageProcessor::process() - " + mDesc);
        dead.put(mRequest);
      }
      else if (queue == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist, message is sent to dead queue";
        mLogger.debug("PutMessageProcessor::process() - " + mDesc);
        dead.put(mRequest);
      }
      else
      {
        boolean success = queue.put(mRequest);
        
        if (!success)
        {
          mDesc = "Failed to put message to queue " + mQueue + ", message is sent to dead queue";
          mLogger.debug("PutMessageProcessor::process() - " + mDesc);
          dead.put(mRequest);
        }
        else
        {
          mDesc = "Message was put to queue " + mQueue;
          mCode = EMqCode.cOkay;
          mLogger.debug("PutMessageProcessor::process() - " + mDesc);
        }
      }
    }
    
    mLogger.debug("PutMessageProcessor::process() - OUT");
    return null;
  }
}
