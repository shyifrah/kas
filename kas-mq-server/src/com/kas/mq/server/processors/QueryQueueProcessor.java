package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqRequestFactory;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

/**
 * Processor for querying information about queues
 * 
 * @author Pippo
 */
public class QueryQueueProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the query originator, the queue name, whether it's a prefix and whether to query all data
   */
  private String mOrigin;
  private boolean mPrefix;
  private boolean mAllData;
  private String mQueue;
  
  /**
   * Construct a {@link QueryQueueProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  QueryQueueProcessor(IMqMessage<?> request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("QueryQueueProcessor::process() - IN");
    
    IMqMessage<?> result;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryQueueProcessor::process() - " + mDesc);
      result = respond();
    }
    else
    {
      mOrigin = mRequest.getStringProperty(IMqConstants.cKasPropertyQryqQmgrName, null);
      mAllData = mRequest.getBoolProperty(IMqConstants.cKasPropertyQryqAllData, false);
      mPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQryqPrefix, false);
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyQryqQueueName, "");
      mLogger.debug("QueryQueueProcessor::process() - Origin=" + StringUtils.asString(mOrigin) +"; Queue=" + mQueue + "; Prefix=" + mPrefix + "; AllData=" + mAllData);
      
      if (mQueue == null) mQueue = "";
      
      Properties props = mRepository.queryQueues(mQueue, mPrefix, mAllData);
      int total = props.size();
      
      mDesc = String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total));
      mValue = props.size();
      mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
      
      result = respond(null, props);
      
      // if this request was originated from a remote qmgr, we process it as if we got a SysState notification
      if (mOrigin != null)
      {
        mLogger.debug("QueryQueueProcessor::process() - Origin is not null, checking if should also handle a sys-state change");
        MqManager manager = mRepository.getRemoteManager(mOrigin);
        if (!manager.isActive())
        {
          IMqMessage<?> sysStateRequest = MqRequestFactory.createSystemStateMessage(mOrigin, true);
          IProcessor processor = new SysStateProcessor(sysStateRequest, mController, mRepository);
          processor.process();
        }
      }
    }
    
    mLogger.debug("QueryQueueProcessor::process() - OUT");
    return result;
  }
}
