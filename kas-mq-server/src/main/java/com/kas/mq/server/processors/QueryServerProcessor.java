package com.kas.mq.server.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.GroupEntity;
import com.kas.sec.entities.GroupEntityDao;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.entities.UserEntityDao;
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
  private UniqueId         mSessionId;         // For Q SESS
  private UniqueId         mConnectionId;      // For Q CONN
  private EQueryConfigType mConfigType;        // For Q CONFIG
  private String           mUserName;          // For Q USER
  private String           mGroupName;         // For Q GROUP
  private String           mOriginQmgr;        // For Q QUEUE
  private String           mQueueName;         // For Q QUEUE
  private boolean          mIsPrefix;          // For Q QUEUE
  private boolean          mIsAllData;         // For Q QUEUE
  private boolean          mIsFormatted;       // For Q QUEUE
  
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
        case QUERY_USER:
          body = queryUser();
          break;
        case QUERY_GROUP:
          body = queryGroup();
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
    
    if ((mQueryType == EQueryType.QUERY_QUEUE) && (mOriginQmgr != null))
    {
      mLogger.debug("QueryServerProcessor::process() - Origin is not null, checking if should also handle a sys-state change");
      MqManager manager = mRepository.getRemoteManager(mOriginQmgr);
      if (!manager.isActive())
      {
        IMqMessage sysStateRequest = MqRequestFactory.createSystemStateMessage(mOriginQmgr, true);
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
    String qConfigType = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryConfigType, "ALL");
    mConfigType = EQueryConfigType.valueOf(qConfigType);
    
    String body = "";
    String resName = String.format("%s_%s", mQueryType.name(), qConfigType);
    if (!isAccessPermitted(EResourceClass.COMMAND, resName))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query configuration";
    }
    else
    {
      switch (mConfigType)
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
    if (sessid != null) mSessionId = UniqueId.fromString(sessid);
    
    StringBuilder sb = new StringBuilder();
    if (!isAccessPermitted(EResourceClass.COMMAND, mQueryType.name()))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query sessions";
    }
    else if (mSessionId != null)
    {
      SessionHandler handler = mController.getHandler(mSessionId);
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
    if (connid != null) mConnectionId = UniqueId.fromString(connid);
    
    StringBuilder sb = new StringBuilder();
    if (!isAccessPermitted(EResourceClass.COMMAND, mQueryType.name()))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query connections";
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
    return sb.toString();
  }

  /**
   * Return the output of the "q queue" command
   * 
   * @return the output of the "q queue" command
   */
  private String queryQueue()
  {
    mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
    mIsAllData = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryAllData, false);
    mIsFormatted = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryFormatOutput, true);
    mQueueName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryQueueName, "");
    if (mQueueName == null) mQueueName = "";
    
    String body = "";
    
    String resName = String.format("%s%s", mQueryType.name(), (mQueueName.length() == 0 ? "" : "_" + mQueueName));
    if (!isAccessPermitted(EResourceClass.COMMAND, resName))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query queues";
    }
    else
    {
      StringList qlist = mRepository.queryQueues(mQueueName, mIsPrefix, mIsAllData);
      int total = qlist.size();
      
      mDesc = String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total));
      mValue = total;
      mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
      
      if (mIsFormatted)
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

  /**
   * Return the output of the "q user" command
   * 
   * @return the output of the "q user" command
   */
  private String queryUser()
  {
    mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
    mUserName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryUserName, "");
    if (mUserName == null) mUserName = "";
    
    String body = "";
    
    if (!isAccessPermitted(EResourceClass.COMMAND, String.format("%s%s", mQueryType.name(), (mUserName.length() == 0 ? "" : "_" + mUserName))))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to issue " + mQueryType.name() + " command";
    }
    else if (!isAccessPermitted(EResourceClass.USER, mUserName.length() == 0 ? "" : mUserName, AccessLevel.READ_ACCESS))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query users";
    }
    else
    {
      List<UserEntity> ulist = null;
      
      if (mIsPrefix && (mUserName.isEmpty()))
      {
        ulist = UserEntityDao.getAll();
      }
      else if (mIsPrefix)
      {
        ulist = UserEntityDao.getByPattern(mUserName);
      }
      else
      {
        ulist = new ArrayList<UserEntity>();
        UserEntity user = UserEntityDao.getByName(mUserName);
        if (user != null)
          ulist.add(user);
      }
      
      int total = ulist == null ? 0 : ulist.size();
      mDesc = String.format("%s users matched filtering criteria", (total == 0 ? "No" : total));
      mValue = total;
      mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
      
      StringBuilder sb = new StringBuilder();
      for (UserEntity ue : ulist)
      {
        sb.append("User.............: ").append(ue.getName()).append('\n');
        sb.append("  ID.........: ").append(ue.getId()).append('\n');
        sb.append("  Description: ").append(ue.getDescription()).append('\n');
        sb.append("  Groups.....: ");
        
        String groups = "\n    N/A";
        List<Integer> gids = ue.getGroups();
        if ((gids != null) && (gids.size() > 0))
        {
          StringBuffer sbg = new StringBuffer();
          for (int gid : gids)
          {
            GroupEntity ge = GroupEntityDao.getById(gid);
            sb.append("\n    ").append(ge.getName()).append(" (").append(ge.getId()).append(")");
          }
          groups = sbg.toString();
        }
        sb.append(groups).append('\n');
      }
      
      sb.append(" \n");
      body = sb.toString();
    }
    
    return body;
  }

  /**
   * Return the output of the "q group" command
   * 
   * @return the output of the "q group" command
   */
  private String queryGroup()
  {
    mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
    mGroupName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryGroupName, "");
    if (mGroupName == null) mGroupName = "";
    
    String body = "";
    
    if (!isAccessPermitted(EResourceClass.COMMAND, String.format("%s%s", mQueryType.name(), (mGroupName.length() == 0 ? "" : "_" + mGroupName))))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to issue " + mQueryType.name() + " command";
    }
    else if (!isAccessPermitted(EResourceClass.GROUP, mGroupName.length() == 0 ? "" : mGroupName, AccessLevel.READ_ACCESS))
    {
      mCode = EMqCode.cError;
      mDesc = "User is not permitted to query groups";
    }
    else
    {
      List<GroupEntity> glist = null;
      
      if (mIsPrefix && (mGroupName.isEmpty()))
      {
        glist = GroupEntityDao.getAll();
      }
      else if (mIsPrefix)
      {
        glist = GroupEntityDao.getByPattern(mGroupName);
      }
      else
      {
        glist = new ArrayList<GroupEntity>();
        GroupEntity group = GroupEntityDao.getByName(mGroupName);
        if (group != null)
          glist.add(group);
      }
      
      int total = glist == null ? 0 : glist.size();
      mDesc = String.format("%s groups matched filtering criteria", (total == 0 ? "No" : total));
      mValue = total;
      mCode = mValue == 0 ? EMqCode.cWarn : EMqCode.cOkay;
      
      StringBuilder sb = new StringBuilder();
      for (GroupEntity ge : glist)
      {
        sb.append("Group............: ").append(ge.getName()).append('\n');
        sb.append("  ID.........: ").append(ge.getId()).append('\n');
        sb.append("  Description: ").append(ge.getDescription()).append('\n');
      }
      
      sb.append(" \n");
      body = sb.toString();
    }
    
    return body;
  }
}
