package com.kas.mq.server.processors;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for deleting queues
 * 
 * @author Pippo
 */
public class DeleteQueueProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * the queue name and if deletion should be forced
   */
  private String mQueue;
  private boolean mForce;
  
  /**
   * Construct a {@link DeleteQueueProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  DeleteQueueProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("DeleteQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
    }
    else
    {
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyDelQueueName, null);
      mForce = mRequest.getBoolProperty(IMqConstants.cKasPropertyDelForce, false);
      mLogger.debug("DeleteQueueProcessor::process() - Queue=" + mQueue + "; Force=" + mForce);
      
      MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);
      
      if (mqlq == null)
      {
        mDesc = "Queue with name \"" + mQueue + "\" doesn't exist";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else if (mqlq.size() == 0)
      {
        mRepository.deleteLocalQueue(mQueue);
        mCode = EMqCode.cOkay;
        mDesc = "Queue with name \"" + mQueue + "\" was successfully deleted";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else if (mForce)
      {
        int size = mqlq.size();
        mRepository.deleteLocalQueue(mQueue);
        mCode = EMqCode.cOkay;
        mDesc = "Queue with name \"" + mQueue + "\" was successfully deleted (" + size + " messages discarded)";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
      else
      {
        int size = mqlq.size();
        mDesc = "Queue is not empty (" + size + " messages) and FORCE was not specified";
        mLogger.debug("DeleteQueueProcessor::process() - " + mDesc);
      }
    }
    
    mLogger.debug("DeleteQueueProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process queue deletion request.<br>
   * <br>
   * If queue deletion was successful, we need to inform remote KAS/MQ managers
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
    mLogger.debug("DeleteQueueProcessor::postprocess() - IN");
    
    if (mCode == EMqCode.cOkay)
    {
      Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
      String localQmgr = mConfig.getManagerName();
      
      for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
      {
        String remoteQmgrName  = entry.getKey();
        NetworkAddress address = entry.getValue();
        
        mLogger.debug("DeleteQueueProcessor::postprocess() - Notifying KAS/MQ server \"" + remoteQmgrName + "\" (" + address.toString() + ") on repository update");
        
        MqServerConnection conn = MqServerConnectionPool.getInstance().allocate();
        conn.connect(address.getHost(), address.getPort());
        if (conn.isConnected())
        {
          boolean success = conn.notifyRepoUpdate(localQmgr, mQueue, false);
          mLogger.debug("DeleteQueueProcessor::postprocess() - Notification returned: " + success);
          
          conn.disconnect();
        }
        MqServerConnectionPool.getInstance().release(conn);
      }
    }
    
    mLogger.debug("DeleteQueueProcessor::postprocess() - OUT");
    return true;
  }
}
