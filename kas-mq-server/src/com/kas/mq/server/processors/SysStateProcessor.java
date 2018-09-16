package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.server.IController;
import com.kas.mq.server.repo.RemoteQueuesManager;

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
   */
  SysStateProcessor(IMqMessage<?> request, IController controller)
  {
    super(request, controller);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("SysStateProcessor::process() - IN");
    
    IMqMessage<?> result = null;
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("SysStateProcessor::process() - " + mDesc);
      result = respond();
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
        ((RemoteQueuesManager)manager).setQueues(remoteQueues);
        
        Properties localQueues = mRepository.queryLocalQueues("", true, false);
        result = respond();
        result.setSubset(localQueues);
      }
      
      if (result == null) result = respond();
    }
    
    mLogger.debug("SysStateProcessor::process() - OUT");
    return result;
  }
}
