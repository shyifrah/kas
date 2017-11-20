package com.kas.q.server.req;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

final public class AuthenticateRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(AuthenticateRequest.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mUserName;
  private String  mPassword;
  private boolean mAdmin;
  private String  mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code GetRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the MessageConsumer.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  AuthenticateRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    String  userName = null;
    String  password = null;
    Boolean admin    = false;
    String  jmsMsgId = null;
    try
    {
      userName = requestMessage.getStringProperty(IKasqConstants.cPropertyUserName);
      password = requestMessage.getStringProperty(IKasqConstants.cPropertyPassword);
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    // mAdmin is optional, extract it separately  
    try
    {
      admin = requestMessage.getBooleanProperty(IKasqConstants.cPropertyAdminMessage);
    }
    catch (Throwable e) {}
    
    if (userName == null)
    {
      sLogger.warn("Received AuthenticateRequest with invalid user: name=[" + StringUtils.asString(userName) + "]");
      throw new IllegalArgumentException("Invalid AuthenticateRequest: null user name");
    }
    
    if (userName.length() == 0)
    {
      sLogger.warn("Received AuthenticateRequest with invalid user: name=[" + StringUtils.asString(userName) + "]");
      throw new IllegalArgumentException("Invalid AuthenticateRequest: user name is empty string");
    }
    
    if (password == null)
    {
      sLogger.warn("Received AuthenticateRequest with invalid password: type=[" + StringUtils.asString(password) + "]");
      throw new IllegalArgumentException("Invalid AuthenticateRequest: null password");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received AuthenticateRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid AuthenticateRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received AuthenticateRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid AuthenticateRequest: JMS message ID is empty string");
    }
    
    mUserName = userName;
    mPassword = password;
    mAdmin = admin;
    mJmsMessageId = jmsMsgId;
  }
  
  /***************************************************************************************************************
   * Get the user name
   * 
   * @return the user name
   */
  public String getUserName()
  {
    return mUserName;
  }
  
  /***************************************************************************************************************
   * Get the password
   * 
   * @return the password
   */
  public String getPassword()
  {
    return mPassword;
  }
  
  /***************************************************************************************************************
   * Get admin property
   * 
   * @return true if admin property was set, false otherwise
   */
  public boolean isAdmin()
  {
    return mAdmin;
  }
  
  /***************************************************************************************************************
   * Get the JMS message ID
   * 
   * @return the JMS message ID
   */
  public String getJmsMessageId()
  {
    return mJmsMessageId;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cAuthenticate;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(UserName=").append(mUserName)
      .append(" Password=").append(mPassword).append(")");
    return sb.toString();
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  UserName=(").append(mUserName).append(")\n")
      .append(pad).append("  Password=(").append(mPassword).append(")\n")
      .append(pad).append("  Admin=(").append(mAdmin).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
