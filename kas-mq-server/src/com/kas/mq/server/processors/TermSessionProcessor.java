package com.kas.mq.server.processors;

import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for terminating an active session
 * 
 * @author Pippo
 */
public class TermSessionProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * Session ID to terminate 
   */
  private UniqueId mSessionId;
  
  /**
   * Construct a {@link TermSessionProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  TermSessionProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process terminate session request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("TermSessionProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("TermSessionProcessor::process() - " + mDesc);
    }
    else
    {
      String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyTermSessId, null);
      if (sessid != null) mSessionId = UniqueId.fromString(sessid);
      mLogger.debug("TermSessionProcessor::process() - SessionId=" + mSessionId);
      
      SessionHandler handler = mController.getHandler(mSessionId);
      if (handler == null)
      {
        mDesc = "Session with ID " + mSessionId + " does not exist";
      }
      else
      {
        handler.end();
        mDesc = "Session with ID " + mSessionId + " was successfully terminated";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.debug("TermSessionProcessor::process() - OUT");
    return respond();
  }
}
