package com.kas.mq.server.processors;

import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.resources.EResourceClass;

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
   * @param handler The session handler
   * @param repository The server's repository
   */
  TermConnectionProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
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
    else if (!isAccessPermitted(EResourceClass.COMMAND, "TERM_CONNECTION"))
    {
      mDesc = "User is not permitted to terminate connections";
      mLogger.warn(mDesc);
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
        mCode = EMqCode.cOkay;
        mDesc = "Connection with ID " + mConnectionId + " was successfully terminated";
        mLogger.debug("TermConnectionProcessor::process() - " + mDesc);
      }
    }
    
    mLogger.debug("TermConnectionProcessor::process() - OUT");
    return respond();
  }
}
