package com.kas.mq.server.processors;

import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.resources.EResourceClass;

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
   * @param handler The session handler
   * @param repository The server's repository
   */
  TermSessionProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
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
    else if (!isAccessPermitted(EResourceClass.COMMAND, "TERM_SESSION"))
    {
      mDesc = "User is not permitted to terminate sessions";
      mLogger.warn(mDesc);
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
        mCode = EMqCode.cOkay;
        mDesc = "Session with ID " + mSessionId + " was successfully terminated";
        mLogger.debug("TermSessionProcessor::process() - " + mDesc);
      }
    }
    
    mLogger.debug("TermSessionProcessor::process() - OUT");
    return respond();
  }
}
