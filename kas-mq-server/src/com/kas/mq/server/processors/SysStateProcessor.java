package com.kas.mq.server.processors;

import java.util.Map;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
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
   * @param repository The server's repository
   */
  SysStateProcessor(IMqMessage<?> request, IController controller, IRepository repository)
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
        Properties sessions = mRequest.getSubset(IMqConstants.cKasPropertySyssSessionPrefix);
        for (Map.Entry<Object, Object> entry : sessions.entrySet())
        {
          String sessId = (String)entry.getValue();
          UniqueId uid = UniqueId.fromString(sessId);
          mController.termHandler(uid);
        }
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
