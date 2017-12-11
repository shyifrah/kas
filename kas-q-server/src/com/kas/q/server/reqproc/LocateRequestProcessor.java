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

final public class LocateRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(LocateRequestProcessor.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage     mMessage;
  private String           mDestinationName;
  private EDestinationType mDestinationType;
  private String           mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code LocateRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  LocateRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
    String  destName = null;
    Integer type = null;
    String  jmsMsgId = null;
    try
    {
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
      type = requestMessage.getIntProperty(IKasqConstants.cPropertyDestinationType);
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    if (destName == null)
    {
      sLogger.warn("Received LocateRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid LocateRequest: null destination name");
    }
    
    if (destName.length() == 0)
    {
      sLogger.warn("Received LocateRequest with invalid destination: name=[" + StringUtils.asString(destName) + "]");
      throw new IllegalArgumentException("Invalid LocateRequest: destination is empty string");
    }
    
    if (type == null)
    {
      sLogger.warn("Received LocateRequest with invalid destination: type=[" + StringUtils.asString(type) + "]");
      throw new IllegalArgumentException("Invalid LocateRequest: null destination type");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received LocateRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid LocateRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received LocateRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid LocateRequest: JMS message ID is empty string");
    }
    
    
    mDestinationType = EDestinationType.fromInt(type);
    mDestinationName = destName;
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
   * @return the destination type
   */
  public EDestinationType getDestinationType()
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
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("LocateRequestProcessor::process() - IN");
    
    sLogger.debug("LocateRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("LocateRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      IKasqDestination dest = null;
      int code = IKasqConstants.cPropertyResponseCode_Fail;
      String msg = "";
      
      // now we address the repository and locate the destination
      sLogger.debug("LocateRequestProcessor::process() - Destination type is " + mDestinationType.toString());
      switch (mDestinationType)
      {
        case cQueue:
          dest = sRepository.locateQueue(mDestinationName);
          break;
        case cTopic:
          dest = sRepository.locateTopic(mDestinationName);
          break;
        default:
          break;
      }
      
      sLogger.debug("LocateRequestProcessor::process() - Located destination: " + StringUtils.asPrintableString(dest));
      
      if (dest == null)
      {
        msg = mDestinationType.toString() + " with name " + mDestinationName + " could not be located";
      }
      else
      {
        code = IKasqConstants.cPropertyResponseCode_Okay;
      }
      
      IKasqMessage message = new KasqMessage();
      message.setJMSMessageID("ID:" + UniqueId.generate().toString());
      message.setJMSCorrelationID(mJmsMessageId);
      message.setJMSDestination(dest);
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
      
      sLogger.debug("LocateRequestProcessor::process() - Sending response message: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("LocateRequestProcessor::process() - OUT, Result=" + result);
    return result;
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
      .append(pad).append("  Dest=(").append(mDestinationType).append(":///").append(mDestinationName).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
