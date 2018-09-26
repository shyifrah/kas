package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqGlobals.EQueryType;
import java.util.Collection;
import com.kas.comm.serializer.Deserializer;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.UniqueId;
import com.kas.logging.impl.AppenderManager;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

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
  private UniqueId   mQuerySessionId;
  
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
    mLogger.debug("QueryServerProcessor::process() - IN");
    
    String body = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryServerProcessor::process() - " + mDesc);
    }
    else
    {
      String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyQrysSessionId, null);
      if (sessid != null) mQuerySessionId = UniqueId.fromString(sessid);
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQrysQueryType, EQueryType.cUnknown.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryServerProcessor::process() - QueryType=" + mQueryType.toString() + "; SessId=" + mQuerySessionId);
      
      mCode = EMqCode.cOkay;
      mDesc = "";
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
        case cQuerySession:
          body = querySession();
          break;
        case cUnknown:
        default:
          mCode = EMqCode.cError;
          mDesc = "Invalid query server information type: " + temp;
          break;
      }
    }
    
    mLogger.debug("QueryServerProcessor::process() - OUT");
    return respond(body, null);
  }
  
  /**
   * Return the output of the "q session" command
   * 
   * @return the output of the "q session" command
   */
  private String querySession()
  {
    String result = null;
    if (mQuerySessionId != null)
    {
      StringBuilder sb = new StringBuilder();
      SessionHandler handler = mController.getHandler(mQuerySessionId);
      if (handler == null)
      {
        sb.append("No handlers displayed");
      }
      else
      {
        sb.append(handler.toPrintableString());
        sb.append("\n1 handlers displayed");
      }
      result = sb.toString();
    }
    else
    {
      StringBuilder sb = new StringBuilder();
      Collection<SessionHandler> col = mController.getHandlers();
      for (SessionHandler handler : col)
        sb.append(handler.toPrintableString()).append('\n');
      sb.append(col.size() + " handlers displayed");
      result = sb.toString();
    }
    return result;
  }
}
