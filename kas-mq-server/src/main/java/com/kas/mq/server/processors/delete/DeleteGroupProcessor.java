package com.kas.mq.server.processors.delete;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.mq.server.processors.AProcessor;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.GroupEntityDao;
import com.kas.sec.resources.EResourceClass;

/**
 * Processor for deleting groups
 * 
 * @author Pippo
 */
public class DeleteGroupProcessor extends AProcessor
{
  /**
   * Input
   */
  private String mGroup;
  
  /**
   * Construct a {@link DeleteGroupProcessor}
   * 
   * @param request
   *   The request message
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  public DeleteGroupProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.trace("DeleteGroupProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.trace("DeleteGroupProcessor::process() - {}", mDesc);
    }
    else
    {
      mGroup = mRequest.getStringProperty(IMqConstants.cKasPropertyDelGroupName, null);
      mLogger.trace("DeleteGroupProcessor::process() - Group={}", mGroup);
      
      if (!isAccessPermitted(EResourceClass.COMMAND, String.format("DELETE_GROUP_%s", mGroup)))
      {
        mDesc = "User is not permitted to issue DELETE_GROUP command";
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(EResourceClass.GROUP, mGroup, AccessLevel.WRITE_ACCESS))
      {
        mDesc = "User is not permitted to delete groups";
        mLogger.warn(mDesc);
      }
      else
      {
        if (GroupEntityDao.getByName(mGroup) == null)
        {
          mDesc = "Group with name " + mGroup + " does not exist";
          mLogger.warn(mDesc);
        }
        else
        {
          GroupEntityDao.delete(mGroup);
          mLogger.trace("DeleteGroupProcessor::process() - Deleted group {}", mGroup);
          mDesc = "Group with name " + mGroup + " was successfully deleted";
          mCode = EMqCode.cOkay;
        }
      }
    }
    
    mLogger.trace("DeleteGroupProcessor::process() - OUT");
    return respond();
  }
}