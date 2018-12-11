package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for terminating the KAS/MQ server
 * 
 * @author Pippo
 */
public class TermServerProcessor extends AProcessor
{
  /**
   * Construct a {@link TermServerProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  TermServerProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process the terminate request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("TermServerProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("TermServerProcessor::process() - " + mDesc);
    }
    else if (!isAccessPermitted(EResourceClass.COMMAND, "TERM_SERVER"))
    {
      mDesc = "User is not permitted to terminate KAS/MQ server";
      mLogger.warn(mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "Shutdown request was successfully posted";
      mLogger.debug("TermServerProcessor::process() - " + mDesc);
    }
    
    mLogger.debug("TermServerProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process the shutdown request.<br>
   * <br>
   * If the completion code is {@link EMqCode#cOkay}, the handler should cease its work.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} if completion code is not okay, {@code false} otherwise 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage, IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    if (mCode == EMqCode.cOkay)
    {
      mController.shutdown();
      return false;
    }
    return true;
  }
}
