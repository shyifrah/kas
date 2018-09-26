package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.comm.serializer.Deserializer;
import com.kas.comm.serializer.SerializerConfiguration;
import com.kas.config.MainConfiguration;
import com.kas.logging.impl.AppenderManager;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

/**
 * Processor for querying server's information
 * 
 * @author Pippo
 */
public class QueryServerProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the type of information
   */
  private EQueryType mQueryType;
  
  /**
   * Construct a {@link QueryServerProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  QueryServerProcessor(IMqMessage<?> request, IController controller, IRepository repository)
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
    
    String body = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryQueueProcessor::process() - " + mDesc);
    }
    else
    {
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQrysQueryType, EQueryType.cUnknown.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryQueueProcessor::process() - QueryType=" + mQueryType.toString());
      
      switch (mQueryType)
      {
        case cQueryConfigAll:
          body = MainConfiguration.getInstance().toPrintableString();
          break;
        case cQueryConfigLogging:
          body = AppenderManager.getInstance().getConfig().toPrintableString();
          break;
        case cQueryConfigMq:
          body = mConfig.toPrintableString();
          break;
        case cQueryConfigSerializer:
          body = Deserializer.getInstance().getConfig().toPrintableString();
          break;
        case cUnknown:
        default:
          mCode = EMqCode.cError;
          mDesc = "Invalid query server information type: " + temp;
          break;
      }
    }
    
    mLogger.debug("QueryQueueProcessor::process() - OUT");
    return respond(body, null);
  }
}
