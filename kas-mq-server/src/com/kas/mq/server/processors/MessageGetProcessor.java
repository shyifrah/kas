package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.impl.internal.MqResponse;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

/**
 * Processor for getting a message from a queue
 * 
 * @author Pippo
 */
public class MessageGetProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * Timeout for get operation, polling interval length and queue name from which to get the message 
   */
  private long mTimeout;
  private long mInterval;
  private String mQueue;
  
  /**
   * Construct a {@link MessageGetProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  MessageGetProcessor(IMqMessage<?> request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process get message request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("MessageGetProcessor::process() - IN");
    
    IMqMessage<?> result = null;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("MessageGetProcessor::process() - " + mDesc);
      result = respond();
    }
    else
    {
      mTimeout  = mRequest.getLongProperty(IMqConstants.cKasPropertyGetTimeout, IMqConstants.cDefaultTimeout);
      mInterval = mRequest.getLongProperty(IMqConstants.cKasPropertyGetInterval, IMqConstants.cDefaultPollingInterval);
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyGetQueueName, null);
      mLogger.debug("MessageGetProcessor::process() - Queue=" + mQueue + "; Timeout=" + mTimeout+ "; Interval=" + mInterval);
      
      MqQueue queue = mRepository.getQueue(mQueue);
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mDesc = "Invalid queue name: null or empty string";
        mLogger.debug("MessageGetProcessor::process() - " + mDesc);
        result = respond();
      }
      else if (queue == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist";
        mLogger.debug("MessageGetProcessor::process() - " + mDesc);
        result = respond();
      }
      else
      {
        result = queue.get(mTimeout, mInterval);
        if (result == null)
        {
          mDesc = "No message found in queue " + mQueue;
          mCode = EMqCode.cWarn;
          mLogger.debug("MessageGetProcessor::process() - " + mDesc);
          result = respond();
        }
        else
        {
          mDesc = "Successfully retrieved message from queue " + mQueue;
          mLogger.debug("MessageGetProcessor::process() - " + mDesc);
          mCode = EMqCode.cOkay;
          result.setResponse(new MqResponse(mCode, mValue, mDesc));
        }
      }
    }
    
    mLogger.debug("MessageGetProcessor::process() - OUT");
    return result;
  }
}
