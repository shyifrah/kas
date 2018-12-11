package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.resources.EResourceClass;

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
   * @param handler The session handler
   * @param repository The server's repository
   */
  MessageGetProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process get message request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("MessageGetProcessor::process() - IN");
    
    UserEntity ue = mHandler.getActiveUser();
    
    IMqMessage result = null;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("MessageGetProcessor::process() - " + mDesc);
    }
    else if (!ue.isAccessPermitted(EResourceClass.COMMAND, "get"))
    {
      mDesc = "User " + ue.toString() + " is not permitted to issue GET requests";
      mLogger.warn(mDesc);
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
      }
      else if (queue == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist";
        mLogger.debug("MessageGetProcessor::process() - " + mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.QUEUE, mQueue, AccessLevel.READ_ACCESS))
      {
        mDesc = "User is not permitted to read from queues";
        mLogger.warn(mDesc);
      }
      else
      {
        result = queue.get(mTimeout, mInterval);
        if (result == null)
        {
          mDesc = "No message found in queue " + mQueue;
          mCode = EMqCode.cWarn;
          mLogger.debug("MessageGetProcessor::process() - " + mDesc);
        }
        else
        {
          mDesc = "Successfully retrieved message from queue " + mQueue;
          mLogger.debug("MessageGetProcessor::process() - " + mDesc);
          mCode = EMqCode.cOkay;
        }
      }
    }
    
    mLogger.debug("MessageGetProcessor::process() - OUT");
    return respond(result);
  }
}
