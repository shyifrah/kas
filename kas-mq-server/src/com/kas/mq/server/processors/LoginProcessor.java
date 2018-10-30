package com.kas.mq.server.processors;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.mq.server.security.EntityManager;
import com.kas.mq.server.security.IUserEntity;

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
   * user's name, the BASE-64 encoded password (in string format), the client application name
   */
  private String mUser;
  private String mSb64Pass;
  private String mClientApp;
  
  /**
   * Construct a {@link LoginProcessor}
   * 
   * @param request The request message
   * @param controller The session controller
   * @param repository The server's repository
   * @param handler The session handler
   */
  LoginProcessor(IMqMessage request, IController controller, IRepository repository, SessionHandler handler)
  {
    super(request, controller, repository);
    mHandler = handler;
  }
  
  /**
   * Process login request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("LoginProcessor::process() - IN");
    
    Properties props = new Properties();
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("LoginProcessor::process() - " + mDesc);
    }
    else
    {
      mUser = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginUserName, null);
      mSb64Pass = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginPassword, null);
      mClientApp = mRequest.getStringProperty(IMqConstants.cKasPropertyLoginAppName, null);
      mLogger.debug("LoginProcessor::process() - ClientApp=" + mClientApp + "; User=" + mUser + "; Pass=" + mSb64Pass);
      
      EntityManager emgr = mController.getEntityManager();
      IUserEntity ue = emgr.getUserByName(mUser);
      
      if ((mUser == null) || (mUser.length() == 0))
      {
        mDesc = "Invalid user name";
      }
      else if (ue == null)
      {
        mDesc = "User " + mUser + " is not defined";
      }
      else
      {
        byte [] userpwd = ue.getPassword();
        String strUserpwd = StringUtils.asHexString(userpwd);
        
        if (!mSb64Pass.equals(strUserpwd))
        {
          mDesc = "Incorrect password for " + mUser;
        }
        else
        {
          mDesc = "User " + mUser + " successfully authenticated";
          mCode = EMqCode.cOkay;
          mHandler.setActiveUserName(mUser);
          props.setStringProperty(IMqConstants.cKasPropertyLoginSession, mHandler.getSessionId().toString());
        }
      }
    }
    
    mLogger.debug("LoginProcessor::process() - OUT");
    return respond(null, props);
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
  public boolean postprocess(IMqMessage reply)
  {
    if (mCode == EMqCode.cOkay)
      return true;
    return false;
  }
}
