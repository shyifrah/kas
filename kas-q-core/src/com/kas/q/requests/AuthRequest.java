package com.kas.q.requests;

import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

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
  public AuthRequest(String userName, String password)
  {
    super();
    mUserName = userName;
    mPassword = password;
    
    if (cAdminUser.equals(mUserName))
      mAdmin = true;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("AuthRequest::setRequestProperties() - IN");
    
    try
    {
      requestMessage.setStringProperty(IKasqConstants.cPropertyUserName, mUserName);
      requestMessage.setStringProperty(IKasqConstants.cPropertyPassword, mPassword);
      requestMessage.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, mAdmin);
    }
    catch (Throwable e)
    {
      mLogger.debug("AuthRequest::setRequestProperties() - JMSException caught: ", e);
    }
    
    mLogger.debug("AuthRequest::setRequestProperties() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cAuthenticate;
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
