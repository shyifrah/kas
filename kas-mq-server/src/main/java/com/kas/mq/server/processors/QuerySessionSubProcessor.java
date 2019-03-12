package com.kas.mq.server.processors;

import java.util.Collection;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying server's connections
 * 
 * @author Pippo
 */
public class QuerySessionSubProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the session ID
   */
  private UniqueId mSessionId;
  
  /**
   * Construct a {@link QuerySessionSubProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QuerySessionSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process query session request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("QuerySessionSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QuerySessionSubProcessor::process() - " + mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyQuerySessId, null);
      if (sessid != null) mSessionId = UniqueId.fromString(sessid);
      
      StringBuilder sb = new StringBuilder();
      String cmdRes = EQueryType.QUERY_SESSION.name();
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query sessions";
        mLogger.debug("QuerySessionSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource=" + cmdRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else if (mSessionId != null)
      {
        SessionHandler handler = mController.getHandler(mSessionId);
        if (handler == null)
        {
          sb.append("No handlers displayed");
        }
        else
        {
          sb.append(handler.toPrintableString());
          sb.append("\n1 handlers displayed");
        }
      }
      else
      {
        Collection<SessionHandler> col = mController.getHandlers();
        for (SessionHandler handler : col)
          sb.append(handler.toPrintableString()).append('\n');
        sb.append(col.size() + " handlers displayed");
      }
      
      body = sb.toString();
    }
    
    mLogger.debug("QuerySessionSubProcessor::process() - OUT");
    return respond(body, props);
  }
}
