package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.UserEntityDao;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for deleting users
 * 
 * @author Pippo
 */
public class DeleteUserProcessor extends AProcessor
{
  /**
   * Extracted input from the request:
   * the user name
   */
  private String mUserName;
  
  /**
   * Construct a {@link DeleteUserProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  DeleteUserProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.debug("DeleteUserProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("DeleteUserProcessor::process() - " + mDesc);
    }
    else
    {
      mUserName = mRequest.getStringProperty(IMqConstants.cKasPropertyDelUserName, null);
      mLogger.debug("DeleteUserProcessor::process() - User=" + mUserName);
      
      if (!isAccessPermitted(EResourceClass.COMMAND, String.format("DELETE_USER_%s", mUserName)))
      {
        mDesc = "User is not permitted to issue DELETE_USER command";
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.USER, mUserName, AccessLevel.WRITE_ACCESS))
      {
        mDesc = "User is not permitted to delete users";
        mLogger.warn(mDesc);
      }
      else
      {
        if (UserEntityDao.getByName(mUserName) == null)
        {
          mDesc = "User with name " + mUserName + " does not exist";
          mLogger.warn(mDesc);
        }
        else
        {
          UserEntityDao.delete(mUserName);
          mLogger.debug("DeleteUserProcessor::process() - Deleted user " + mUserName);
          mDesc = "User with name " + mUserName + " was successfully deleted";
          mCode = EMqCode.cOkay;
        }
      }
    }
    
    mLogger.debug("DeleteUserProcessor::process() - OUT");
    return respond();
  }
}
