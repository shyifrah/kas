package com.kas.mq.server.processors;

import com.kas.mq.impl.IMqGlobals.EQueryType;
import java.util.Collection;
import java.util.Map;
import com.kas.comm.serializer.Deserializer;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.logging.impl.AppenderManager;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqManager;
import com.kas.mq.impl.internal.MqRequestFactory;
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
  private EQueryType mQueryType;         // For all Q command variations
  private UniqueId   mQuerySessionId;    // For Q SESS
  private UniqueId   mQueryConnectionId; // For Q CONN
  private String     mQueryOriginQmgr;   // For Q QUEUE
  private String     mQueryQueue;        // For Q QUEUE
  private boolean    mQueryQueuePrefix;  // For Q QUEUE
  private boolean    mQueryAllData;      // For Q QUEUE
  private boolean    mQueryOutProps;     // For Q QUEUE
  
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
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryServerProcessor::process() - " + mDesc);
    }
    else
    {
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQrysQueryType, EQueryType.cUnknown.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryServerProcessor::process() - QueryType=" + mQueryType.toString());
      
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
          
        case cQueryQueue:
          props = queryQueue();
          if (!mQueryOutProps)
          {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Object, Object> entry : props.entrySet())
              sb.append(entry.getValue().toString()).append('\n');
            body = sb.toString();
          }
          break;
          
        case cUnknown:
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
  public boolean postprocess(IMqMessage<?> reply)
  {
    mLogger.debug("QueryServerProcessor::postprocess() - IN");
    
    if ((mQueryType == EQueryType.cQueryQueue) && (mQueryOriginQmgr != null))
    {
      mLogger.debug("QueryServerProcessor::process() - Origin is not null, checking if should also handle a sys-state change");
      MqManager manager = mRepository.getRemoteManager(mQueryOriginQmgr);
      if (!manager.isActive())
      {
        IMqMessage<?> sysStateRequest = MqRequestFactory.createSystemStateMessage(mQueryOriginQmgr, true);
        IProcessor processor = new SysStateProcessor(sysStateRequest, mController, mRepository);
        processor.process();
      }
    }
    
    mLogger.debug("QueryServerProcessor::postprocess() - OUT");
    return true;
  }
  
  /**
   * Return the output of the "q session" command
   * 
   * @return the output of the "q session" command
   */
  private String querySession()
  {
    String sessid = mRequest.getStringProperty(IMqConstants.cKasPropertyQrysSessionId, null);
    if (sessid != null) mQuerySessionId = UniqueId.fromString(sessid);
    
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
    String connid = mRequest.getStringProperty(IMqConstants.cKasPropertyQrysConnId, null);
    if (connid != null) mQueryConnectionId = UniqueId.fromString(connid);
    
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

  /**
   * Return the output of the "q queue" command
   * 
   * @return the output of the "q queue" command
   */
  private Properties queryQueue()
  {
    mQueryOriginQmgr = mRequest.getStringProperty(IMqConstants.cKasPropertyQryqQmgrName, null);
    mQueryQueuePrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQryqPrefix, false);
    mQueryAllData = mRequest.getBoolProperty(IMqConstants.cKasPropertyQryqAllData, false);
    mQueryOutProps = mRequest.getBoolProperty(IMqConstants.cKasPropertyQryqOutOnlyProps, false);
    mQueryQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyQryqQueueName, "");
    if (mQueryQueue == null) mQueryQueue = "";
    
    Properties props = mRepository.queryQueues(mQueryQueue, mQueryQueuePrefix, mQueryAllData);
    int total = props.size();
    
    mDesc = String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total));
    mValue = props.size();
    mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
    
    return props;
  }
}
