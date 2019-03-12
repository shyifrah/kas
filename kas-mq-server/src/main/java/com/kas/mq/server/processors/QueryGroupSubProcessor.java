package com.kas.mq.server.processors;

import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.GroupEntity;
import com.kas.sec.entities.GroupEntityDao;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for querying user groups information
 * 
 * @author Pippo
 */
public class QueryGroupSubProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the group name, is it a prefix
   */
  private String  mGroupName;
  private boolean mIsPrefix;
  
  /**
   * Construct a {@link QueryGroupSubProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryGroupSubProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("QueryGroupSubProcessor::process() - IN");
    
    String body = null;
    Properties props = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryGroupSubProcessor::process() - " + mDesc);
    }
    else
    {
      mCode = EMqCode.cOkay;
      mDesc = "";
      
      mIsPrefix = mRequest.getBoolProperty(IMqConstants.cKasPropertyQueryPrefix, false);
      mGroupName = mRequest.getStringProperty(IMqConstants.cKasPropertyQueryGroupName, "");
      if (mGroupName == null) mGroupName = "";
      
      body = "";
      
      String cmdRes = String.format("%s%s", EQueryType.QUERY_GROUP.name(), (mGroupName.length() == 0 ? "" : "_" + mGroupName));
      String grpRes = mGroupName.length() == 0 ? "" : mGroupName;
      if (!isAccessPermitted(EResourceClass.COMMAND, cmdRes))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to issue " + EQueryType.QUERY_GROUP.name() + " command";
        mLogger.debug("QueryGroupSubProcessor::process() - Insufficient permissions: ResClass=COMMAND; Resource=" + cmdRes + "; Access=" + AccessLevel.READ_ACCESS);
      }
      else if (!isAccessPermitted(EResourceClass.GROUP, grpRes, AccessLevel.READ_ACCESS))
      {
        mCode = EMqCode.cError;
        mDesc = "User is not permitted to query groups";
        mLogger.debug("QueryGroupSubProcessor::process() - Insufficient permissions: ResClass=GROUP; Resource=" + grpRes + "; Access=" + AccessLevel.READ_ACCESS);
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
    }
    
    mLogger.debug("QueryGroupSubProcessor::process() - OUT");
    return respond(body, props);
  }
}
