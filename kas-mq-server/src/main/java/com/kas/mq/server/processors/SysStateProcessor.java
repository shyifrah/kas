package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.StringList;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqManager;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
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
   * @param handler The session handler
   * @param repository The server's repository
   */
  SysStateProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("SysStateProcessor::process() - IN");
    
    Properties props = new Properties();
    StringList localQueueList = null;
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
        String remoteQueues = mRequest.getStringProperty(IMqConstants.cKasPropertySyssQueueList, null);
        if (remoteQueues != null)
        {
          StringList remoteQueueList = StringList.fromString(remoteQueues);
          ((MqRemoteManager)manager).setQueues(remoteQueueList);
        }
        
        localQueueList = mRepository.queryLocalQueues("", true, false);
        props.setStringProperty(IMqConstants.cKasPropertySyssQueueList, localQueueList.toString());
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
