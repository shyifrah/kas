package com.kas.mq.server.processors;

import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
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
 * Processor for altering queues
 * 
 * @author Chen
 */
public class AlterQueueProcessor extends AProcessor
{
 
/**
   * Extracted input from the request:
   * queue name and its entire properties to be processed later
   */
  private String mQueue;
  private Properties qProps;
  
  /**
   * Construct a {@link AlterQueueProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  AlterQueueProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue alter request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("AlterQueueProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("AlterQueueProcessor::process() - " + mDesc);
    }
    else
    {    	    	
        mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyAltQueueName, null);
        Properties qProps = mRequest.getSubset("");
    	
        MqLocalQueue mqlq = mRepository.getLocalQueue(mQueue);      
    	if (mqlq == null)
    	{
    		mDesc = "Queue with name \"" + mQueue + "\" does not exists";		
    		mLogger.debug("AlterQueueProcessor::process() - " + mDesc);
    	}
    	else
    	{
    		mqlq = mRepository.alterLocalQueue(mQueue, qProps);
    		mLogger.debug("AlterQueueProcessor::process() - Altered queue " + mqlq);
    		mCode = EMqCode.cOkay;
    	}
    }    
    mLogger.debug("AlterQueueProcessor::process() - OUT");
    return respond();
  }
  
  /**
   * Post-process queue alter request.<br>
   * <br>
   * If queue alter was successful, we need to inform remote KAS/MQ managers
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
    mLogger.debug("AlterQueueProcessor::postprocess() - IN");
    
    if (mCode == EMqCode.cOkay)
    {
      Map<String, NetworkAddress> map = mConfig.getRemoteManagers();
      String localQmgr = mConfig.getManagerName();
      
      for (Map.Entry<String, NetworkAddress> entry : map.entrySet())
      {
        String remoteQmgrName  = entry.getKey();
        NetworkAddress address = entry.getValue();
        
        mLogger.debug("AlterQueueProcessor::postprocess() - Notifying KAS/MQ server \"" + remoteQmgrName + "\" (" + address.toString() + ") on repository update");
        
        MqServerConnection conn = MqServerConnectionPool.getInstance().allocate();
        conn.connect(address.getHost(), address.getPort());
        if (conn.isConnected())
        {
          boolean success = conn.notifyRepoUpdate(localQmgr, mQueue, true);
          mLogger.debug("AlterQueueProcessor::postprocess() - Notification returned: " + success);
          
          conn.disconnect();
        }
        MqServerConnectionPool.getInstance().release(conn);
      }
    }
    
    mLogger.debug("AlterQueueProcessor::postprocess() - OUT");
    return true;
  }
}
