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
  private boolean mAdmin;
  
  /***************************************************************************************************************
   *  
   */
  public AuthRequest(String userName, String password) throws JMSException
  {
    super(ERequestType.cAuthenticate);
    
    if (cAdminUser.equals(userName))
      mAdmin = true;
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    setStringProperty(IKasqConstants.cPropertyUserName, userName);
    setStringProperty(IKasqConstants.cPropertyPassword, password);
    setBooleanProperty(IKasqConstants.cPropertyAdminMessage, mAdmin);
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
}
