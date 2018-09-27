package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

/**
 * Processor for shutting down the KAS/MQ server
 * 
 * @author Pippo
 */
public class ShutdownProcessor extends AProcessor
{
  /**
   * Construct a {@link ShutdownProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  ShutdownProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process shutdown request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("ShutdownProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("ShutdownProcessor::process() - " + mDesc);
    }
    else
    {
      String user = mRequest.getStringProperty(IMqConstants.cKasPropertyShutUserName, IMqConstants.cSystemUserName);
      mDesc = "Cannot shutdown KAS/MQ server with non-admin user";
      if ("admin".equalsIgnoreCase(user))
      {
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
