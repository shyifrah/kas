package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for altering queues
 * 
 * @author Chen
 */
public class AlterQueueProcessor extends AProcessor
{
 
  /**
   * Extracted input from the request:
   * queue name
   */
  private String mQueue;
  
  /**
   * Construct a {@link AlterQueueProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  AlterQueueProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue alter request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("AlterQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("AlterQueueProcessor::process() - " + mDesc);
    }
    else
    {    	    	
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyAltQueueName, null);
      Properties qProps = mRequest.getSubset(IMqConstants.cKasPropertyPrefix + "alt.q.opt.");
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      if (qProps.size() == 0)
      {
        mDesc = "At least one queue property must be specified";
        mLogger.debug("AlterQueueProcessor::process() - " + mDesc);
      }
      else if (mqlq == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" does not exists";
        mLogger.debug("AlterQueueProcessor::process() - " + mDesc);
      }
      else
      {
        mqlq = mRepository.alterLocalQueue(mQueue, qProps);
        mLogger.debug("AlterQueueProcessor::process() - Altered queue " + StringUtils.asPrintableString(mqlq));
        mDesc = "Queue " + mQueue + " was successfully altered";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.debug("AlterQueueProcessor::process() - OUT");
    return respond();
  }
}
