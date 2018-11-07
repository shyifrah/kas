package com.kas.mq.server.processors;

import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;

/**
 * Processor for terminating an active connection
 * 
 * @author Pippo
 */
public class TermConnectionProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * Connection ID to terminate 
   */
  private UniqueId mConnectionId;
  
  /**
   * Construct a {@link TermConnectionProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  TermConnectionProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    super(request, controller, repository);
  }
  
  /**
   * Process terminate connection request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("TermConnectionProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("TermConnectionProcessor::process() - " + mDesc);
    }
    else
    {
      String connid = mRequest.getStringProperty(IMqConstants.cKasPropertyTermConnId, null);
      if (connid != null) mConnectionId = UniqueId.fromString(connid);
      mLogger.debug("TermConnectionProcessor::process() - ConnectionId=" + mConnectionId);
    
      MqServerConnectionPool pool = MqServerConnectionPool.getInstance();
      MqServerConnection conn = pool.getConnection(mConnectionId);
      if (conn == null)
      {
        mDesc = "Connection with ID " + mConnectionId + " does not exist";
      }
      else
      {
        pool.release(conn);
        mDesc = "Connection with ID " + mConnectionId + " was successfully terminated";
        mCode = EMqCode.cOkay;
      }
    }
    
    mLogger.debug("TermConnectionProcessor::process() - OUT");
    return respond();
  }
}