package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqManager;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.repo.MqRemoteManager;

/**
 * Processor for handling system-state change
 * 
 * @author Pippo
 */
public class SysStateProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the sys-state change originator and the change type.
   */
  private String mOrigin;
  private boolean mActivated;
  
  /**
   * Construct a {@link SysStateProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  SysStateProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("SysStateProcessor::process() - IN");
    
    Properties props = null;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("SysStateProcessor::process() - " + mDesc);
    }
    else
    {
      mOrigin = mRequest.getStringProperty(IMqConstants.cKasPropertySyssQmgrName, null);
      mActivated = mRequest.getBoolProperty(IMqConstants.cKasPropertySyssActive, false);
      mLogger.debug("SysStateProcessor::process() - Originator=" + mOrigin + "; State=" + (mActivated ? "Activated" : "Deactivated"));
      
      mDesc = "Manager at " + mOrigin + " changed its state to " + (mActivated ? "active" : "inactive");
      mCode = EMqCode.cOkay;
      
      MqManager manager = mRepository.getRemoteManager(mOrigin);
      if (!mActivated && manager.isActive())
      {
        manager.deactivate();
      }
      else if (mActivated && !manager.isActive())
      {
        manager.activate();
        Properties remoteQueues = mRequest.getSubset(IMqConstants.cKasPropertyQryqResultPrefix);
        ((MqRemoteManager)manager).setQueues(remoteQueues);
        
        props = mRepository.queryLocalQueues("", true, false);
      }
    }
    
    mLogger.debug("SysStateProcessor::process() - OUT");
    return respond(null, props);
  }
  
  /**
   * Post-process request.<br>
   * <br>
   * DO NOT ADD A POST-PROCESS FOR SYS-STATE PROCESSOR.
   * Its {@link #process()} is also called from {@link QueryServerProcessor}, which means there might
   * not be a possibility for its {@link #postprocess(IMqMessage)} to execute.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return Always {@code true} 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    return true;
  }
}
