package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

final public class QueryRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(QueryRequestProcessor.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage     mMessage;
  private String           mJmsMessageId;
  private EDestinationType mDestinationType;
  private String           mDestinationName;
  private Integer          mPriority;
  
  /***************************************************************************************************************
   * Construct a {@code GetRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  QueryRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
    Integer type = null;
    String  jmsMsgId = null;
    String  destName = null;
    Integer priority = null;
    try
    {
      jmsMsgId = requestMessage.getJMSMessageID();
      type     = requestMessage.getIntProperty(IKasqConstants.cPropertyDestinationType);
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
      priority = requestMessage.getIntProperty(IKasqConstants.cPropertyPriority);
    }
    catch (Throwable e) {}
    
    if (type == null)
    {
      sLogger.warn("Received QueryRequest with invalid destination: type=[" + StringUtils.asString(type) + "]");
      throw new IllegalArgumentException("Invalid QueryRequest: null destination type");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received QueryRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid QueryRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received QueryRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid QueryRequest: JMS message ID is empty string");
    }
    
    mJmsMessageId = jmsMsgId;
    mDestinationType = EDestinationType.fromInt(type);
    
    try
    {
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
    }
    catch (Throwable e) {}
    mDestinationName = destName;
    
    try
    {
      priority = requestMessage.getIntProperty(IKasqConstants.cPropertyPriority);
    }
    catch (Throwable e) {}
    mPriority = priority;
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
   * Get the destination type
   * 
   * @return the destination type
   */
  public EDestinationType getDestinationType()
  {
    return mDestinationType;
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
   * Get the consumer queue name
   * 
   * @return the consumer queue name
   */
  public Integer getPriority()
  {
    return mPriority;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("QueryRequestProcessor::process() - IN");
    
    sLogger.debug("QueryRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("QueryRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      IKasqMessage message = null;
      int code = IKasqConstants.cPropertyResponseCode_Fail;
      
      
      
      // now we address the repository and locate the destination
      sLogger.debug("QueryRequestProcessor::process() - Sending response message: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("QueryRequestProcessor::process() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cQuery;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Dest=(").append(mDestinationType).append(":///").append(mDestinationName).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
