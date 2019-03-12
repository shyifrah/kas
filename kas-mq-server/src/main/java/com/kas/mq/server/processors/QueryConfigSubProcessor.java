package com.kas.mq.server.processors;

import com.kas.config.MainConfiguration;
import com.kas.db.DbConnectionPool;
import com.kas.infra.base.Properties;
import com.kas.logging.impl.LogSystem;
import com.kas.mq.impl.EQueryConfigType;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying server's configuration
 * 
 * @author Pippo
 */
class QueryConfigSubProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the configuration type
   */
  private EQueryConfigType mConfigType;
  
  /**
   * Construct a {@link QueryConfigSubProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryConfigSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process query config request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("QueryConfigSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryConfigSubProcessor::process() - " + mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      String qConfigType = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryConfigType, "ALL");
      mConfigType = EQueryConfigType.valueOf(qConfigType);
      
      String cmdRes = String.format("%s_%s", EQueryType.QUERY_CONFIG.name(), qConfigType);
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query configuration";
        mLogger.debug("QueryQueueSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource=" + cmdRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else
      {
        switch (mConfigType)
        {
          case ALL:
            body = MainConfiguration.getInstance().toPrintableString();
            break;
          case LOGGING:
            body = LogSystem.getInstance().getConfig().toPrintableString();
            break;
          case MQ:
            body = mConfig.toPrintableString();
            break;
          case DB:
            body = DbConnectionPool.getInstance().getConfig().toPrintableString();
            break;
        }
      }
    }
    
    mLogger.debug("QueryConfigSubProcessor::process() - OUT");
    return respond(body, props);
  }
}
