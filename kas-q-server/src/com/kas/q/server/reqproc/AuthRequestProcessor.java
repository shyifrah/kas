package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;

final public class AuthRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(AuthRequestProcessor.class);
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage mMessage;
  private String  mUserName;
  private String  mPassword;
  private boolean mAdmin;
  private String  mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code AuthRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the MessageConsumer.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  AuthRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
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
      sLogger.warn("Received AuthRequest with invalid user: name=[" + StringUtils.asString(userName) + "]");
      throw new IllegalArgumentException("Invalid AuthRequest: null user name");
    }
    
    if (userName.length() == 0)
    {
      sLogger.warn("Received AuthRequest with invalid user: name=[" + StringUtils.asString(userName) + "]");
      throw new IllegalArgumentException("Invalid AuthRequest: user name is empty string");
    }
    
    if (password == null)
    {
      sLogger.warn("Received AuthRequest with invalid password: type=[" + StringUtils.asString(password) + "]");
      throw new IllegalArgumentException("Invalid AuthRequest: null password");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received AuthRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid AuthRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received AuthRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid AuthRequest: JMS message ID is empty string");
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
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("AuthRequestProcessor::process() - IN");
    boolean authenticated = false;
    
    //String userName = request.getUserName();
    //String password = request.getPassword();
    // TODO: address some security manager and find out if the credentials are okay
    //       if they are okay, set authenticated to "true".
    //
    sLogger.debug("AuthRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    authenticated = true;
    String msg = "";
    int code = IKasqConstants.cPropertyResponseCode_Okay;
    
    IKasqMessage response = new KasqMessage();
    response.setJMSMessageID("ID:" + UniqueId.generate().toString());
    response.setJMSCorrelationID(mJmsMessageId);
    response.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
    response.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
    
    sLogger.debug("AuthRequestProcessor::process() - Sending response message: " + response.toPrintableString(0));
    handler.send(response);
    
    sLogger.debug("AuthRequestProcessor::process() - OUT, Result=" + authenticated);
    return authenticated;
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
