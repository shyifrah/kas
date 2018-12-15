package com.kas.mq.server.processors;

import java.util.Collection;
import com.kas.config.MainConfiguration;
import com.kas.db.DbConnectionPool;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.StringList;
import com.kas.logging.impl.LogSystem;
import com.kas.mq.impl.EQueryConfigType;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqManager;
import com.kas.mq.internal.MqRequestFactory;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.MqServerConnection;
import com.kas.mq.server.internal.MqServerConnectionPool;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.resources.EResourceClass;

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
  private EQueryType       mQueryType;         // For all Q command variations
  private UniqueId         mQuerySessionId;    // For Q SESS
  private UniqueId         mQueryConnectionId; // For Q CONN
  private EQueryConfigType mQueryConfigType;   // For Q CONFIG
  private String           mQueryOriginQmgr;   // For Q QUEUE
  private String           mQueryQueue;        // For Q QUEUE
  private boolean          mQueryQueuePrefix;  // For Q QUEUE
  private boolean          mQueryAllData;      // For Q QUEUE
  private boolean          mQueryFormat;       // For Q QUEUE
  
  /**
   * Construct a {@link QueryServerProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryServerProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("QueryServerProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryServerProcessor::process() - " + mDesc);
    }
    else
    {
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQueryType, EQueryType.UNKNOWN.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryServerProcessor::process() - QueryType=" + mQueryType.toString());
      
      mCode = EMqCode.cOkay;
      mDesc = "";
      switch (mQueryType)
      {
        case QUERY_CONFIG:
          body = queryConfig();
          break;
        case QUERY_SESSION:
          body = querySession();
          break;
        case QUERY_CONNECTION:
          body = queryConnection();
          break;
        case QUERY_QUEUE:
          body = queryQueue();
          break;
        case UNKNOWN:
        default:
          mCode = EMqCode.cError;
          mDesc = "Invalid query server information type: " + temp;
          break;
      }
    }
    
    mLogger.debug("QueryServerProcessor::process() - OUT");
    return respond(body, props);
  }
  
  /**
   * Post-process queue query request.<br>
   * <br>
   * If a query request came from a remote qmgr, we also process it
   * as if we got a sys-state request.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return always {@code true} 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    mLogger.debug("QueryServerProcessor::postprocess() - IN");
    
    if ((mQueryType == EQueryType.QUERY_QUEUE) && (mQueryOriginQmgr != null))
    {
      mLogger.debug("QueryServerProcessor::process() - Origin is not null, checking if should also handle a sys-state change");
      MqManager manager = mRepository.getRemoteManager(mQueryOriginQmgr);
      if (!manager.isActive())
      {
        IMqMessage sysStateRequest = MqRequestFactory.createSystemStateMessage(mQueryOriginQmgr, true);
        IProcessor processor = new SysStateProcessor(sysStateRequest, mHandler, mRepository);
        processor.process();
      }
    }
    
    mLogger.debug("QueryServerProcessor::postprocess() - OUT");
    return true;
  }
  
  /**
   * Return the output of the "q config" command
   * 
   * @return the output of the "q config" command
   */
  private String queryConfig()
  {
    int qConfigType = mRequest.getIntProperty(IMqConstants.cKasPropertyQueryConfigType, -1);
    if (qConfigType != -1) mQueryConfigType = EQueryConfigType.fromInt(qConfigType);
    
    String body = "";
    String resName = String.format("%s_%s", mQueryType.name(), mQueryConfigType.name());
    if (!isAccessPermitted(EResourceClass.COMMAND, resName))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query configuration";
    }
    else
    {
      switch (mQueryConfigType)
      {
        case ALL:
          body = MainConfiguration.getInstance().toPrintableString();
          break;
        case LOGGING:
          body = LogSystem.getInstance().getConfig().toPrintableString();
          break;
        case MQ:
          body = mConfig.toPrintableString();
          break;
        case DB:
          body = DbConnectionPool.getInstance().getConfig().toPrintableString();
          break;
      }
    }
    
    return body;
  }
  
  /**
   * Return the output of the "q session" command
   * 
   * @return the output of the "q session" command
   */
  private String querySession()
  {
    String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyQuerySessId, null);
    if (sessid != null) mQuerySessionId = UniqueId.fromString(sessid);
    
    StringBuilder sb = new StringBuilder();
    if (!isAccessPermitted(EResourceClass.COMMAND, mQueryType.name()))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query sessions";
    }
    else if (mQuerySessionId != null)
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
    String connid = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryConnId, null);
    if (connid != null) mQueryConnectionId = UniqueId.fromString(connid);
    
    StringBuilder sb = new StringBuilder();
    if (!isAccessPermitted(EResourceClass.COMMAND, mQueryType.name()))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query connections";
    }
    else if (mQueryConnectionId != null)
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

  /**
   * Return the output of the "q queue" command
   * 
   * @return the output of the "q queue" command
   */
  private String queryQueue()
  {
    mQueryQueuePrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
    mQueryAllData = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryAllData, false);
    mQueryFormat = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryFormatOutput, true);
    mQueryQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryQueueName, "");
    if (mQueryQueue == null) mQueryQueue = "";
    
    String body = "";
    
    String resName = String.format("%s%s", mQueryType.name(), (mQueryQueue.length() == 0 ? "" : "_" + mQueryQueue));
    if (!isAccessPermitted(EResourceClass.COMMAND, resName))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query connections";
    }
    else
    {
      StringList qlist = mRepository.queryQueues(mQueryQueue, mQueryQueuePrefix, mQueryAllData);
      int total = qlist.size();
      
      mDesc = String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total));
      mValue = total;
      mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
      
      if (mQueryFormat)
      {
        StringBuilder sb = new StringBuilder();
        for (String str : qlist)
          sb.append("Queue.............: ").append(str).append('\n');
        
        sb.append(" \n");
        body = sb.toString();
      }
      else
      {
        body = qlist.toString();
      }
    }
    
    return body;
  }
}
