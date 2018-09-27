package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqGlobals.EQueryType;
import java.util.Collection;
import com.kas.comm.serializer.Deserializer;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.UniqueId;
import com.kas.logging.impl.AppenderManager;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for querying server's information
 * 
 * @author Pippo
 */
public class QueryServerProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the type of information
   */
  private EQueryType mQueryType;
  private UniqueId   mQuerySessionId;
  private UniqueId   mQueryConnectionId;
  
  /**
   * Construct a {@link QueryServerProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   */
  QueryServerProcessor(IMqMessage<?> request, IController controller, IRepository repository)
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
    mLogger.debug("QueryServerProcessor::process() - IN");
    
    String body = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryServerProcessor::process() - " + mDesc);
    }
    else
    {
      String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyQrysSessionId, null);
      if (sessid != null) mQuerySessionId = UniqueId.fromString(sessid);
      String connid = mRequest.getStringProperty(IMqConstants.cKasPropertyQrysConnId, null);
      if (connid != null) mQueryConnectionId = UniqueId.fromString(connid);
      
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQrysQueryType, EQueryType.cUnknown.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryServerProcessor::process() - QueryType=" + mQueryType.toString() + "; SessId=" + mQuerySessionId + "; ConnId=" + mQueryConnectionId);
      
      mCode = EMqCode.cOkay;
      mDesc = "";
      switch (mQueryType)
      {
        case cQueryConfigAll:
          body = MainConfiguration.getInstance().toPrintableString();
          break;
        case cQueryConfigLogging:
          body = AppenderManager.getInstance().getConfig().toPrintableString();
          break;
        case cQueryConfigMq:
          body = mConfig.toPrintableString();
          break;
        case cQueryConfigSerializer:
          body = Deserializer.getInstance().getConfig().toPrintableString();
          break;
        case cQuerySession:
          body = querySession();
          break;
        case cQueryConnection:
          body = queryConnection();
          break;
        case cUnknown:
        default:
          mCode = EMqCode.cError;
          mDesc = "Invalid query server information type: " + temp;
          break;
      }
    }
    
    mLogger.debug("QueryServerProcessor::process() - OUT");
    return respond(body, null);
  }
  
  /**
   * Return the output of the "q session" command
   * 
   * @return the output of the "q session" command
   */
  private String querySession()
  {
    StringBuilder sb = new StringBuilder();
    if (mQuerySessionId != null)
    {
      SessionHandler handler = mController.getHandler(mQuerySessionId);
      if (handler == null)
      {
        sb.append("No handlers displayed");
      }
      else
      {
        sb.append(handler.toPrintableString());
        sb.append("\n1 handlers displayed");
      }
    }
    else
    {
      Collection<SessionHandler> col = mController.getHandlers();
      for (SessionHandler handler : col)
        sb.append(handler.toPrintableString()).append('\n');
      sb.append(col.size() + " handlers displayed");
    }
    return sb.toString();
  }
  
  /**
   * Return the output of the "q connection" command
   * 
   * @return the output of the "q connection" command
   */
  private String queryConnection()
  {
    StringBuilder sb = new StringBuilder();
    if (mQueryConnectionId != null)
    {
      MqServerConnection conn = MqServerConnectionPool.getInstance().getConnection(mQueryConnectionId);
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
    return sb.toString();
  }
}
