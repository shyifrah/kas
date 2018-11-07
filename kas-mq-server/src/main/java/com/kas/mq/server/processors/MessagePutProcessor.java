package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

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
   * @param repository The server's repository
   */
  MessagePutProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process put message request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("MessagePutProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mLogger.debug("MessagePutProcessor::process() - KAS/MQ server is disabled");
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyPutQueueName, null);
      mLogger.debug("MessagePutProcessor::process() - Queue=" + mQueue);
      
      MqQueue queue = mRepository.getQueue(mQueue);
      MqLocalQueue dead = mRepository.getDeadQueue();
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mDesc = "Invalid queue name: null or empty string";
        mLogger.debug("MessagePutProcessor::process() - " + mDesc);
        dead.put(mRequest);
      }
      else if (queue == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist, message is sent to dead queue";
        mLogger.debug("MessagePutProcessor::process() - " + mDesc);
        dead.put(mRequest);
      }
      else
      {
        boolean success = queue.put(mRequest);
        
        if (!success)
        {
          mDesc = "Failed to put message to queue " + mQueue + ", message is sent to dead queue";
          mLogger.debug("MessagePutProcessor::process() - " + mDesc);
          dead.put(mRequest);
        }
        else
        {
          mDesc = "Message was put to queue " + mQueue;
          mCode = EMqCode.cOkay;
          mLogger.debug("MessagePutProcessor::process() - " + mDesc);
        }
      }
    }
    
    mLogger.debug("MessagePutProcessor::process() - OUT");
    return respond();
  }
}