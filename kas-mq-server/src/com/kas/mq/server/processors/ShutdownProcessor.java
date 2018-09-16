package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.server.IController;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for shutting down the KAS/MQ server
 * 
 * @author Pippo
 */
public class ShutdownProcessor extends AProcessor
{
  /**
   * The session handler
   */
  private SessionHandler mHandler;
  
  /**
   * Construct a {@link ShutdownProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param handler The session handler
   */
  ShutdownProcessor(IMqMessage<?> request, IController controller, SessionHandler handler)
  {
    super(request, controller);
    mHandler = handler;
  }
  
  /**
   * Process shutdown request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("ShutdownProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("ShutdownProcessor::process() - " + mDesc);
    }
    else
    {
      mDesc = "Cannot shutdown KAS/MQ server with non-admin user";
      if ("admin".equalsIgnoreCase(mHandler.getActiveUserName()))
      {
        mController.shutdown();
        mCode = EMqCode.cOkay;
        mDesc = "Shutdown request was successfully posted";
        mLogger.debug("ShutdownProcessor::process() - " + mDesc);
      }
    }
    
    mLogger.debug("ShutdownProcessor::process() - OUT");
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
  public boolean postprocess(IMqMessage<?> reply)
  {
    if (mCode == EMqCode.cOkay)
      return false;
    return true;
  }
}
