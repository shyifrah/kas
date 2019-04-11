package com.kas.mq.server.processors.query;

import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.mq.server.processors.AProcessor;
import com.kas.mq.server.processors.QueryServerProcessor;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.GroupEntity;
import com.kas.sec.entities.GroupEntityDao;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.entities.UserEntityDao;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying users information
 * 
 * @author Pippo
 */
public class QueryUserSubProcessor extends AProcessor
{
  /**
   * Input
   */
  private String  mUserName;
  private boolean mIsPrefix;
  
  /**
   * Construct a {@link QueryServerProcessor}
   * 
   * @param request
   *   The request message
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  public QueryUserSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process request
   * 
   * @return
   *   response message generated by {@link #respond()}
   */
  public IMqMessage process()
  {
    mLogger.trace("QueryUserSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.trace("QueryUserSubProcessor::process() - {}", mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
      mUserName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryUserName, "");
      if (mUserName == null) mUserName = "";
      
      body = "";
      
      String cmdRes = String.format("%s%s", EQueryType.QUERY_USER.name(), (mUserName.length() == 0 ? "" : "_" + mUserName));
      String usrRes = mUserName.length() == 0 ? "" : mUserName;
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to issue " + EQueryType.QUERY_USER.name() + " command";
        mLogger.trace("QueryUserSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource={}; Access={}", cmdRes, AccessLevel.READ_ACCESS);
      }
      else if (!isAccessPermitted(EResourceClass.USER, usrRes, AccessLevel.READ_ACCESS))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query users";
        mLogger.trace("QueryUserSubProcessor::process() - Insufficient permissions: ResClass=USER; Resource={}; Access={}", usrRes, AccessLevel.READ_ACCESS);
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
    }
    
    mLogger.trace("QueryUserSubProcessor::process() - OUT");
    return respond(body, props);
  }
}
