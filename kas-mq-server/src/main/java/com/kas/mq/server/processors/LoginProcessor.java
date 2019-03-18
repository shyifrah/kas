package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
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
 * Processor for login requests
 * 
 * @author Pippo
 */
public class LoginProcessor extends AProcessor
{
  /**
   * Input
   */
  private String mUser;
  private String mPass;
  private String mClientApp;
  
  /**
   * Construct a {@link LoginProcessor}
   * 
   * @param request
   *   The request message
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  LoginProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
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
    mLogger.trace("LoginProcessor::process() - IN");
    
    Properties props = new Properties();
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.trace("LoginProcessor::process() - {}", mDesc);
    }
    else
    {
      mUser = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginUserName, null);
      mPass = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginPassword, null);
      mClientApp = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginAppName, null);
      mLogger.trace("LoginProcessor::process() - ClientApp={}, User={}, Pass={}", mClientApp, mUser, mPass);
      
      UserEntity ue = UserEntityDao.getByName(mUser);
      
      if ((mUser == null) || (mUser.length() == 0))
      {
        mDesc = "Invalid user name";
        mLogger.warn(mDesc);
      }
      else if (ue == null)
      {
        mDesc = "User " + mUser + " is not defined";
        mLogger.warn(mDesc);
      }
      else if (!ue.isPasswordMatch(mPass))
      {
        mDesc = "Incorrect password for " + mUser;
        mLogger.warn(mDesc);
      }
      else if (!isAccessPermitted(ue, EResourceClass.APPLICATION, mClientApp, AccessLevel.READ_ACCESS))
      {
        mDesc = "User " + ue.toString() + " is not permitted to access application " + mClientApp;
        mLogger.warn(mDesc);
      }
      else
      {
        mDesc = "User " + mUser + " successfully authenticated";
        mCode = EMqCode.cOkay;
        mHandler.setActiveUser(ue);
        props.setStringProperty(IMqConstants.cKasPropertyLoginSession, mHandler.getSessionId().toString());
      }
    }
    
    mLogger.trace("LoginProcessor::process() - OUT");
    return respond(null, props);
  }
  
  /**
   * Post-process the login request.<br>
   * If the completion code was not {@link EMqCode#cOkay}, the handler should cease its work.
   * 
   * @param reply
   *   The reply message the processor's {@link #process()} method generated
   * @return
   *   {@code true} if completion code is okay, {@code false} otherwise
   */
  public boolean postprocess(IMqMessage reply)
  {
    if (mCode == EMqCode.cOkay)
      return true;
    return false;
  }
}
