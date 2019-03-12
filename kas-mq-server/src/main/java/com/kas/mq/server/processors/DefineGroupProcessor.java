package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
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
 * Processor for defining groups
 * 
 * @author Pippo
 */
public class DefineGroupProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * group name, description,
   */
  private String mGroup;
  private String mDescription;
  
  /**
   * Construct a {@link DefineGroupProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  DefineGroupProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process group definition request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("DefineGroupProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DefineGroupProcessor::process() - " + mDesc);
    }
    else
    {
      mGroup = mRequest.getStringProperty(IMqConstants.cKasPropertyDefGroupName, null);
      mDescription = mRequest.getStringProperty(IMqConstants.cKasPropertyDefGroupDesc, "");
      mLogger.debug("DefineGroupProcessor::process() - Group=" + mGroup);
      
      if (!isAccessPermitted(EResourceClass.COMMAND, String.format("DEFINE_GROUP_%s", mGroup)))
      {
        mDesc = "User is not permitted to issue DEFINE_GROUP command";
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.GROUP, mGroup, AccessLevel.WRITE_ACCESS))
      {
        mDesc = "User is not permitted to define groups";
        mLogger.warn(mDesc);
      }
      else
      {
        if (GroupEntityDao.getByName(mGroup) != null)
        {
          mDesc = "Group with name " + mGroup + " already exists";
          mLogger.warn(mDesc);
        }
        else
        {
          GroupEntity ge = GroupEntityDao.create(mGroup, mDescription);
          mLogger.debug("DefineGroupProcessor::process() - Created group " + StringUtils.asPrintableString(ge));
          mDesc = "Group with name " + mGroup + " was successfully defined";
          mCode = EMqCode.cOkay;
        }
      }
    }
    
    mLogger.debug("DefineGroupProcessor::process() - OUT");
    return respond();
  }
}
