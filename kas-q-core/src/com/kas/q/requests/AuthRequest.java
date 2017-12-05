package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IKasqConstants;

public class AuthRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  private static final String cAdminUser = "admin";
  
  /***************************************************************************************************************
   *  
   */
  private String  mUserName;
  private String  mPassword;
  private boolean mAdmin;
  
  /***************************************************************************************************************
   *  
   */
  public AuthRequest(String userName, String password) throws JMSException
  {
    super(ERequestType.cAuthenticate);
    mUserName = userName;
    mPassword = password;
    if (cAdminUser.equals(mUserName))
      mAdmin = true;
    
    mMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
    mMessage.setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setup()
  {
    mLogger.debug("AuthRequest::setup() - IN");
    
    try
    {
      mMessage.setStringProperty(IKasqConstants.cPropertyUserName, mUserName);
      mMessage.setStringProperty(IKasqConstants.cPropertyPassword, mPassword);
      mMessage.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, mAdmin);
    }
    catch (Throwable e)
    {
      mLogger.debug("AuthRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("AuthRequest::setup() - OUT");
  }
  
  /***************************************************************************************************************
   * Indicates if request is for administrator privileges
   * 
   * @return true if user's name is {@code admin}, false otherwise
   */
  public boolean isAdmin()
  {
    return mAdmin;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
