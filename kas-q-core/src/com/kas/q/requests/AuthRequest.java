package com.kas.q.requests;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class AuthRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   *  
   */
  private static final String cAdminUser = "admin";
  
  /***************************************************************************************************************
   *  
   */
  private ILogger mLogger;
  private String  mUserName;
  private String  mPassword;
  private boolean mAdmin;
  
  /***************************************************************************************************************
   *  
   */
  public AuthRequest(String userName, String password)
  {
    mLogger   = LoggerFactory.getLogger(this.getClass());
    mUserName = userName;
    mPassword = password;
    
    if (cAdminUser.equals(mUserName))
      mAdmin = true;
  }
  
  /***************************************************************************************************************
   *  
   */
  public IKasqMessage createRequestMessage()
  {
    mLogger.debug("AuthRequest::createRequestMessage() - IN");
    
    IKasqMessage requestMessage = null;
    
    try
    {
      requestMessage = new KasqMessage();
      requestMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
      requestMessage.setIntProperty(IKasqConstants.cPropertyRequestType, getRequestType().ordinal());
      requestMessage.setStringProperty(IKasqConstants.cPropertyUserName, mUserName);
      requestMessage.setStringProperty(IKasqConstants.cPropertyPassword, mPassword);
      requestMessage.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, mAdmin);
    }
    catch (Throwable e)
    {
      mLogger.debug("KasqConnection::createRequestMessage() - JMSException caught: ", e);
    }
    
    mLogger.debug("AuthRequest::createRequestMessage() - OUT, requestMessage=" + StringUtils.asPrintableString(requestMessage));
    return requestMessage;
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
  public String toString()
  {
    return null;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
