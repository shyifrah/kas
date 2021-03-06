package com.kas.mq.server.processors.define;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.mq.server.processors.AProcessor;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for defining queues
 * 
 * @author Pippo
 */
public class DefineQueueProcessor extends AProcessor
{
  /**
   * Input
   */
  private String mQueue;
  private String mDescription;
  private int mThreshold;
  private EQueueDisp mDisposition;
  
  /**
   * Construct a {@link DefineQueueProcessor}
   * 
   * @param request
   *   The request message
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  public DefineQueueProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process request
   * 
   * @return
   *   response message generated by {@link #respond()}
   */
  public IMqMessage process()
  {
    mLogger.trace("DefineQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.trace("DefineQueueProcessor::process() - {}", mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyDefQueueName, null);
      mDescription = mRequest.getStringProperty(IMqConstants.cKasPropertyDefQueueDesc, "");
      mThreshold = mRequest.getIntProperty(IMqConstants.cKasPropertyDefThreshold, IMqConstants.cDefaultQueueThreshold);
      String disp = mRequest.getStringProperty(IMqConstants.cKasPropertyDefDisposition, EQueueDisp.TEMPORARY.name());
      mDisposition = EQueueDisp.fromString(disp);
      mLogger.trace("DefineQueueProcessor::process() - Queue={}; Threshold={}; Disposition={}", mQueue, mThreshold, disp);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      
      if (mqlq != null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" already exists";
        mLogger.trace("DefineQueueProcessor::process() - {}", mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.COMMAND, String.format("DEFINE_QUEUE_%s", mQueue)))
      {
        mDesc = "User is not permitted to issue DEFINE_QUEUE command";
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.QUEUE, mQueue, AccessLevel.ALTER_ACCESS))
      {
        mDesc = "User is not permitted to define queues";
        mLogger.warn(mDesc);
      }
      else
      {
        mqlq = mRepository.defineLocalQueue(mQueue, mDescription, mThreshold, mDisposition);
        mLogger.trace("DefineQueueProcessor::process() - Created queue {}", StringUtils.asPrintableString(mqlq));
        mDesc = "Queue with name " + mQueue + " and threshold of " + mThreshold + " was successfully defined";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.trace("DefineQueueProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process queue definition request.<br>
   * If queue definition was successful, we need to inform remote KAS/MQ managers
   * that the local repository was updated and that they should update their repository
   * to reflect these changes.
   * 
   * @param reply
   *   The reply message the processor's {@link #process()} method generated
   * @return
   *   {@code true} in case the handler should continue process next request,
   *   {@code false} if it should terminate
   */
  public boolean postprocess(IMqMessage reply)
  {
    mLogger.trace("DefineQueueProcessor::postprocess() - IN");
    
    if (mCode == EMqCode.cOkay)
    {
      Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
      String localQmgr = mConfig.getManagerName();
      
      MqServerConnection conn = MqServerConnectionPool.getInstance().allocate();
      
      for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
      {
        String remoteQmgrName  = entry.getKey();
        NetworkAddress address = entry.getValue();
        
        mLogger.trace("DefineQueueProcessor::postprocess() - Notifying KAS/MQ server \"{}\" ({}) on repository update", remoteQmgrName, address);
        
        conn.connect(address.getHost(), address.getPort());
        if (conn.isConnected())
        {
          boolean logged = conn.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
          if (!logged)
          {
            mLogger.trace("DefineQueueProcessor::postprocess() - Failed to login to remote KAS/MQ server at {}", address);
            continue;
          }
          
          boolean success = conn.notifyRepoUpdate(localQmgr, mQueue, true);
          mLogger.trace("DefineQueueProcessor::postprocess() - Notification returned: {}", success);
          
          conn.disconnect();
        }
      }
      
      MqServerConnectionPool.getInstance().release(conn);
    }
    
    mLogger.trace("DefineQueueProcessor::postprocess() - OUT");
    return true;
  }
}
