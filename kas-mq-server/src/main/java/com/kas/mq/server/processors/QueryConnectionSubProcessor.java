package com.kas.mq.server.processors;

import java.util.Collection;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying server's connections
 * 
 * @author Pippo
 */
public class QueryConnectionSubProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the connection ID
   */
  private UniqueId mConnectionId;
  
  /**
   * Construct a {@link QueryConnectionSubProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryConnectionSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("QueryConnectionSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryConnectionSubProcessor::process() - " + mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      String connid = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryConnId, null);
      if (connid != null) mConnectionId = UniqueId.fromString(connid);
      
      StringBuilder sb = new StringBuilder();
      
      String cmdRes = EQueryType.QUERY_CONNECTION.name();
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query connections";
        mLogger.debug("QueryConnectionSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource=" + cmdRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else if (mConnectionId != null)
      {
        MqServerConnection conn = MqServerConnectionPool.getInstance().getConnection(mConnectionId);
        if (conn == null)
        {
          sb.append("No connections displayed");
        }
        else
        {
          sb.append(conn.toPrintableString());
          sb.append("\n1 connections displayed");
        }
      }
      else
      {
        Collection<MqServerConnection> col = MqServerConnectionPool.getInstance().getConnections();
        for (MqServerConnection conn : col)
          sb.append(conn.toPrintableString()).append('\n');
        sb.append(col.size() + " connections displayed");
      }
      body = sb.toString();
    }
    
    mLogger.debug("QueryConnectionSubProcessor::process() - OUT");
    return respond(body, props);
  }
}
