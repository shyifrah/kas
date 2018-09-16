package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for login requests
 * 
 * @author Pippo
 */
public class LoginProcessor extends AProcessor
{
  /**
   * The session's handler
   */
  private SessionHandler mHandler;
  
  /**
   * Extracted input from the request:
   * user's name and the BASE-64 encoded password (in string format) and the 
   */
  private String mUser;
  private String mSb64Pass;
  
  /**
   * Construct a {@link LoginProcessor}
   * 
   * @param controller The session controller
   * @param handler The session handler
   */
  LoginProcessor(IMqMessage<?> request, IController controller, SessionHandler handler)
  {
    super(request, controller);
    mHandler = handler;
  }
  
  /**
   * Process login request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage<?> process()
  {
    mLogger.debug("LoginProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("LoginProcessor::process() - " + mDesc);
    }
    else
    {
      mUser = mRequest.getStringProperty(IMqConstants.cKasPropertyUserName, null);
      mSb64Pass = mRequest.getStringProperty(IMqConstants.cKasPropertyPassword, null);
      mLogger.debug("LoginProcessor::process() - User=" + mUser + "; Pass=" + mSb64Pass);
      
      byte [] confPwd = mController.getConfig().getUserPassword(mUser);
      String sb64ConfPass = StringUtils.asHexString(confPwd);
      
      if ((mUser == null) || (mUser.length() == 0))
        mDesc = "Invalid user name";
      else if (confPwd == null)
        mDesc = "User " + mUser + " is not defined";
      else if (!mSb64Pass.equals(sb64ConfPass))
        mDesc = "Incorrect password for " + mUser;
      else
      {
        mDesc = "User " + mUser + " successfully authenticated";
        mCode = EMqCode.cOkay;
        mHandler.setActiveUserName(mUser);
      }
    }
    
    IMqMessage<?> result = respond();
    result.setStringProperty(IMqConstants.cKasPropertySessionId, mHandler.getSessionId().toString());
    
    mLogger.debug("LoginProcessor::process() - OUT");
    return result;
  }
  
  /**
   * Post-process the login request.<br>
   * <br>
   * If the completion code was not {@link EMqCode#cOkay}, the handler should cease its work.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} if completion code is okay, {@code false} otherwise 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage, IMqMessage)
   */
  public boolean postprocess(IMqMessage<?> reply)
  {
    if (mCode == EMqCode.cOkay)
      return true;
    return false;
  }
}
