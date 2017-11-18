package com.kas.q.server.req;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class GetRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(GetRequest.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mDestinationName;
  private Integer mDestinationType;
  private String  mOriginQueueName;
  private String  mOriginSessionId;
  
  /***************************************************************************************************************
   * Construct a {@code GetRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  GetRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    String  destName = null;
    Integer destType = null;
    String  originQueue   = null;
    String  originSession = null;
    try
    {
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
      destType = requestMessage.getIntProperty(IKasqConstants.cPropertyDestinationType);
      originQueue   = requestMessage.getStringProperty(IKasqConstants.cPropertyConsumerQueue);
      originSession = requestMessage.getStringProperty(IKasqConstants.cPropertyConsumerSession);
    }
    catch (Throwable e) {}
    
    if (destName == null)
    {
      sLogger.warn("Received GetRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null destination name");
    }
    
    if (destName.length() == 0)
    {
      sLogger.warn("Received GetRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: destination is empty string");
    }
    
    if (destType == null)
    {
      sLogger.warn("Received GetRequest with invalid destination: type=[" + StringUtils.asString(destType) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null destination type");
    }
    
    if (originQueue == null)
    {
      sLogger.warn("Received GetRequest with invalid origin: queue=[" + StringUtils.asString(originQueue) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null origin queue");
    }
    
    if (originQueue.length() == 0)
    {
      sLogger.warn("Received GetRequest with invalid origin: queue=[" + StringUtils.asString(originQueue) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: queue is empty string");
    }
    
    if (originSession == null)
    {
      sLogger.warn("Received GetRequest with invalid origin: session=[" + StringUtils.asString(originSession) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null origin session");
    }
    
    if (originSession.length() == 0)
    {
      sLogger.warn("Received GetRequest with invalid origin: session=[" + StringUtils.asString(originSession) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: session is empty string");
    }
    
    mDestinationName = destName;
    mDestinationType = destType;
    mOriginQueueName = originQueue;
    mOriginSessionId = originSession;
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
   * Get the origin queue name
   * 
   * @return the origin queue name
   */
  public String getOriginQueueName()
  {
    return mOriginQueueName;
  }
  
  /***************************************************************************************************************
   * Get the origin session ID
   * 
   * @return the origin session ID
   */
  public String getOriginSessionId()
  {
    return mOriginSessionId;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cGet;
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
      .append(pad).append("  Origin Queue=(").append(mOriginQueueName).append(")\n")
      .append(pad).append("  Origin Session=(").append(mOriginSessionId).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
