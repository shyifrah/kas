package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.StringList;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqRequestFactory;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying queue information
 * 
 * @author Pippo
 */
public class QueryQueueSubProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * originated manager, queue name, is it a prefix, whether to query all data, whether to format output
   */
  private String   mOriginQmgr;
  private String   mQueueName;
  private boolean  mIsPrefix;
  private boolean  mIsAllData;
  private boolean  mIsFormatted;
  
  /**
   * Construct a {@link QueryQueueSubProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryQueueSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("QueryQueueSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryQueueSubProcessor::process() - " + mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
      mIsAllData = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryAllData, false);
      mIsFormatted = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryFormatOutput, true);
      mQueueName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryQueueName, "");
      if (mQueueName == null) mQueueName = "";
      
      String cmdRes = String.format("%s%s", EQueryType.QUERY_QUEUE.name(), (mQueueName.length() == 0 ? "" : "_" + mQueueName));
      String queRes = mQueueName.length() == 0 ? "" : mQueueName;
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to issue " + EQueryType.QUERY_QUEUE.name() + " command";
        mLogger.debug("QueryQueueSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource=" + cmdRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else if (!isAccessPermitted(EResourceClass.QUEUE, queRes, AccessLevel.READ_ACCESS))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query queues";
        mLogger.debug("QueryUserSubProcessor::process() - Insufficient permissions: ResClass=QUEUE; Resource=" + queRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else
      {
        StringList qlist = mRepository.queryQueues(mQueueName, mIsPrefix, mIsAllData);
        int total = qlist.size();
        
        mDesc = String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total));
        mValue = total;
        mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
        
        if (mIsFormatted)
        {
          StringBuilder sb = new StringBuilder();
          for (String str : qlist)
            sb.append("Queue.............: ").append(str).append('\n');
          
          sb.append(" \n");
          body = sb.toString();
        }
        else
        {
          body = qlist.toString();
        }
      }
    }
    
    mLogger.debug("QueryQueueSubProcessor::process() - OUT");
    return respond(body, props);
  }
  
  /**
   * Post-process queue query request.<br>
   * <br>
   * If a query request came from a remote qmgr, we also process it
   * as if we got a sys-state request.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return always {@code true} 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    mLogger.debug("QueryQueueSubProcessor::postprocess() - IN");
    
    if (mOriginQmgr != null)
    {
      mLogger.debug("QueryQueueSubProcessor::process() - Origin is not null, checking if should also handle a sys-state change");
      MqManager manager = mRepository.getRemoteManager(mOriginQmgr);
      if (!manager.isActive())
      {
        IMqMessage sysStateRequest = MqRequestFactory.createSystemStateMessage(mOriginQmgr, true);
        IProcessor processor = new SysStateProcessor(sysStateRequest, mHandler, mRepository);
        processor.process();
      }
    }
    
    mLogger.debug("QueryQueueSubProcessor::postprocess() - OUT");
    return true;
  }
}
