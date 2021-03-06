package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for putting a message into a queue
 * 
 * @author Pippo
 */
public class MessagePutProcessor extends AProcessor
{
  /**
   * Input
   */
  private String mQueue;
  
  /**
   * Construct a {@link MessagePutProcessor}
   * 
   * @param request
   *   The request message
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  MessagePutProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process request
   * 
   * @return
   *   response message generated by {@link #respond()}
   */
  public IMqMessage process()
  {
    mLogger.trace("MessagePutProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.trace("MessagePutProcessor::process() - {}", mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyPutQueueName, null);
      mLogger.trace("MessagePutProcessor::process() - Queue={}", mQueue);
      
      MqQueue queue = mRepository.getQueue(mQueue);
      MqLocalQueue dead = mRepository.getDeadQueue();
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mDesc = "Invalid queue name: null or empty string";
        mLogger.trace("MessagePutProcessor::process() - {}", mDesc);
        dead.put(mRequest);
      }
      else if (queue == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist, message is sent to dead queue";
        mLogger.trace("MessagePutProcessor::process() - {}", mDesc);
        dead.put(mRequest);
      }
      else if (!isAccessPermitted(EResourceClass.QUEUE, mQueue, AccessLevel.WRITE_ACCESS))
      {
        mDesc = "User is not permitted to write to queues";
        mLogger.warn(mDesc);
      }
      else
      {
        boolean success = queue.put(mRequest);
        
        if (!success)
        {
          mDesc = "Failed to put message to queue " + mQueue + ", message is sent to dead queue";
          mLogger.trace("MessagePutProcessor::process() - {}", mDesc);
          dead.put(mRequest);
        }
        else
        {
          mDesc = "Message was put to queue " + mQueue;
          mCode = EMqCode.cOkay;
          mLogger.trace("MessagePutProcessor::process() - {}", mDesc);
        }
      }
    }
    
    mLogger.trace("MessagePutProcessor::process() - OUT");
    return respond();
  }
}
