package com.kas.q.server.req;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

final public class DefineRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(DefineRequest.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mDestinationName;
  private Integer mDestinationType;
  private String  mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code DefineRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  DefineRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    String  destName = null;
    Integer destType = null;
    String  jmsMsgId = null;
    try
    {
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
      destType = requestMessage.getIntProperty(IKasqConstants.cPropertyDestinationType);
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    if (destName == null)
    {
      sLogger.warn("Received DefineRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: null destination name");
    }
    
    if (destName.length() == 0)
    {
      sLogger.warn("Received DefineRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: destination is empty string");
    }
    
    if (destType == null)
    {
      sLogger.warn("Received DefineRequest with invalid destination: type=[" + StringUtils.asString(destType) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: null destination type");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received DefineRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received DefineRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: JMS message ID is empty string");
    }
    
    mDestinationName = destName;
    mDestinationType = destType;
    mJmsMessageId = jmsMsgId;
  }
  
  /***************************************************************************************************************
   * Get the destination name
   * 
   * @return the destination name
   */
  public String getDestinationName()
  {
    return mDestinationName;
  }
  
  /***************************************************************************************************************
   * Get the destination type
   * 
   * @return the destination type. 1 if it's queue, 2 if it's topic
   */
  public int getDestinationType()
  {
    return mDestinationType;
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
    return ERequestType.cDefine;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(Dest=").append(mDestinationType == 1 ? "queue:///" : "topic:///").append(mDestinationName).append(")");
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
      .append(pad).append("  Destintation Name=(").append(mDestinationName).append(")\n")
      .append(pad).append("  Destintation Type=(").append(mDestinationType).append(")\n")
      .append(pad).append("  Request MessageId=(").append(mJmsMessageId).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
