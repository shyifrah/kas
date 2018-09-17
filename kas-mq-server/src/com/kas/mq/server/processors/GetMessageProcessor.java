package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.impl.internal.MqResponse;
import com.kas.mq.server.IController;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for getting a message from a queue
 * 
 * @author Pippo
 */
public class GetMessageProcessor extends AProcessor
{
  /**
   * The session's handler
   */
  private SessionHandler mHandler;
  
  /**
   * Extracted input from the request:
   * Timeout for get operation, polling interval length and queue name from which to get the message 
   */
  private long mTimeout;
  private long mInterval;
  private String mQueue;
  
  /**
   * Construct a {@link GetMessageProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param handler The session handler
   */
  GetMessageProcessor(IMqMessage<?> request, IController controller, SessionHandler handler)
  {
    super(request, controller);
    mHandler = handler;
  }
  
  /**
   * Process get message request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("GetMessageProcessor::process() - IN");
    
    IMqMessage<?> result = null;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("GetMessageProcessor::process() - " + mDesc);
      result = respond();
    }
    else
    {
      mTimeout  = mRequest.getLongProperty(IMqConstants.cKasPropertyGetTimeout, IMqConstants.cDefaultTimeout);
      mInterval = mRequest.getLongProperty(IMqConstants.cKasPropertyGetInterval, IMqConstants.cDefaultPollingInterval);
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyGetQueueName, null);
      mLogger.debug("GetMessageProcessor::process() - Queue=" + mQueue + "; Timeout=" + mTimeout+ "; Interval=" + mInterval);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      if ((mQueue == null) || (mQueue.length() == 0))
      {
        mDesc = "Invalid queue name: null or empty string";
        mLogger.debug("GetMessageProcessor::process() - " + mDesc);
        result = respond();
      }
      else if (mqlq == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist";
        mLogger.debug("GetMessageProcessor::process() - " + mDesc);
        result = respond();
      }
      else
      {
        result = mqlq.get(mTimeout, mInterval);
        if (result == null)
        {
          mDesc = "No message found in queue " + mQueue;
          mCode = EMqCode.cWarn;
          mLogger.debug("GetMessageProcessor::process() - " + mDesc);
          result = respond();
        }
        else
        {
          mLogger.debug("GetMessageProcessor::process() - " + mDesc);
          mCode = EMqCode.cOkay;
          result.setResponse(new MqResponse(mCode, mValue, mDesc));
        }
        mqlq.setLastAccess(mHandler.getActiveUserName(), "get");
      }
    }
    
    mLogger.debug("GetMessageProcessor::process() - OUT");
    return result;
  }
}
