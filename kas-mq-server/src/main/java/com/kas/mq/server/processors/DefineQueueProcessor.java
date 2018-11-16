package com.kas.mq.server.processors;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for defining queues
 * 
 * @author Pippo
 */
public class DefineQueueProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * queue name, its threshold and is it a permanent queue
   */
  private String mQueue;
  private int mThreshold;
  private boolean mPermanent;
  
  /**
   * Construct a {@link DefineQueueProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  DefineQueueProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue definition request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("DefineQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DefineQueueProcessor::process() - " + mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyDefQueueName, null);
      mThreshold = mRequest.getIntProperty(IMqConstants.cKasPropertyDefThreshold, IMqConstants.cDefaultQueueThreshold);
      mPermanent = mRequest.getBoolProperty(IMqConstants.cKasPropertyDefPermanent, IMqConstants.cDefaultQueuePermanent);
      mLogger.debug("DefineQueueProcessor::process() - Queue=" + mQueue + "; Threshold=" + mThreshold + "; Permanent=" + mPermanent);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      
      if (mqlq != null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" already exists";
        mLogger.debug("DefineQueueProcessor::process() - " + mDesc);
      }
      else
      {
        mqlq = mRepository.defineLocalQueue(mQueue, mThreshold, mPermanent);
        mLogger.debug("DefineQueueProcessor::process() - Created queue " + StringUtils.asPrintableString(mqlq));
        mDesc = "Queue with name " + mQueue + " and threshold of " + mThreshold + " was successfully defined";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.debug("DefineQueueProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process queue definition request.<br>
   * <br>
   * If queue definition was successful, we need to inform remote KAS/MQ managers
   * that the local repository was updated and that they should update their repository
   * to reflect these changes.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} in case the handler should continue process next request,
   * {@code false} if it should terminate 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage, IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    mLogger.debug("DefineQueueProcessor::postprocess() - IN");
    
    if (mCode == EMqCode.cOkay)
    {
      Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
      String localQmgr = mConfig.getManagerName();
      
      for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
      {
        String remoteQmgrName  = entry.getKey();
        NetworkAddress address = entry.getValue();
        
        mLogger.debug("DefineQueueProcessor::postprocess() - Notifying KAS/MQ server \"" + remoteQmgrName + "\" (" + address.toString() + ") on repository update");
        
        MqServerConnection conn = MqServerConnectionPool.getInstance().allocate();
        conn.connect(address.getHost(), address.getPort());
        if (conn.isConnected())
        {
          boolean success = conn.notifyRepoUpdate(localQmgr, mQueue, true);
          mLogger.debug("DefineQueueProcessor::postprocess() - Notification returned: " + success);
          
          conn.disconnect();
        }
        MqServerConnectionPool.getInstance().release(conn);
      }
    }
    
    mLogger.debug("DefineQueueProcessor::postprocess() - OUT");
    return true;
  }
}
