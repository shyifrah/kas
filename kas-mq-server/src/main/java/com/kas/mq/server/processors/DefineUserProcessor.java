package com.kas.mq.server.processors;

import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.entities.UserEntityDao;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for defining users
 * 
 * @author Pippo
 */
public class DefineUserProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * user name, password, group list and description.
   */
  private String mUserName;
  private String mPassword;
  private String mDescription;
  private StringList mGroups;
  
  /**
   * Construct a {@link DefineUserProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  DefineUserProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("DefineUserProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DefineUserProcessor::process() - " + mDesc);
    }
    else
    {
      mUserName = mRequest.getStringProperty(IMqConstants.cKasPropertyDefUserName, null);
      mPassword = mRequest.getStringProperty(IMqConstants.cKasPropertyDefUserPass, null);
      mDescription = mRequest.getStringProperty(IMqConstants.cKasPropertyDefUserDesc, "");
      String str = mRequest.getStringProperty(IMqConstants.cKasPropertyDefUserGrps, "");
      mGroups = StringList.fromString(str);
      
      mLogger.debug("DefineUserProcessor::process() - User=" + mUserName + "; GroupList=" + mGroups);
      
      if (!isAccessPermitted(EResourceClass.COMMAND, String.format("DEFINE_USER_%s", mUserName)))
      {
        mDesc = "User is not permitted to issue DEFINE_USER command";
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.USER, mUserName, AccessLevel.WRITE_ACCESS))
      {
        mDesc = "User is not permitted to define users";
        mLogger.warn(mDesc);
      }
      else
      {
        if (UserEntityDao.getByName(mUserName) != null)
        {
          mDesc = "User with name " + mUserName + " already exists";
          mLogger.warn(mDesc);
        }
        else
        {
          UserEntity ue = UserEntityDao.create(mUserName, mPassword, mDescription, mGroups);
          mLogger.debug("DefineUserProcessor::process() - Created user " + StringUtils.asPrintableString(ue));
          mDesc = "User with name " + mUserName + " was successfully defined";
          mCode = EMqCode.cOkay;
        }
      }
    }
    
    mLogger.debug("DefineUserProcessor::process() - OUT");
    return respond();
  }
}