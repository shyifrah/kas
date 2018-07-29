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

final public class GetRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(GetRequestProcessor.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage     mMessage;
  private String           mDestinationName;
  private EDestinationType mDestinationType;
  private String           mJmsMessageId;
  private String           mConsumerQueue;
  private String           mConsumerSession;
  private Boolean          mNoLocal;
  private String           mSelector;
  
  /***************************************************************************************************************
   * Construct a {@code GetRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  GetRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
    String  destName = null;
    Integer type = null;
    String  jmsMsgId = null;
    String  consQueue = null;
    try
    {
      destName = requestMessage.getStringProperty(IKasqConstants.cPropertyDestinationName);
      type = requestMessage.getIntProperty(IKasqConstants.cPropertyDestinationType);
      jmsMsgId = requestMessage.getJMSMessageID();
      consQueue = requestMessage.getStringProperty(IKasqConstants.cPropertyConsumerQueue);
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
    
    if (type == null)
    {
      sLogger.warn("Received GetRequest with invalid destination: type=[" + StringUtils.asString(type) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null destination type");
    }
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received GetRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received GetRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: JMS message ID is empty string");
    }
    
    if (consQueue == null)
    {
      sLogger.warn("Received GetRequest with invalid Consumer Queue: name=[" + StringUtils.asString(consQueue) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: null Consumer queue");
    }
    
    if (consQueue.length() == 0)
    {
      sLogger.warn("Received GetRequest with invalid Consumer Queue: name=[" + StringUtils.asString(consQueue) + "]");
      throw new IllegalArgumentException("Invalid GetRequest: consumer queue is empty string");
    }
    
    mDestinationType = EDestinationType.fromInt(type);
    mDestinationName = destName;
    mJmsMessageId = jmsMsgId;
    mConsumerQueue = consQueue;
    
    String  session = null;
    boolean noLocal = false;
    String  selector = null;
    try
    {
      session = requestMessage.getStringProperty(IKasqConstants.cPropertyConsumerSession);
    }
    catch (Throwable e) {}
    mConsumerSession = session;
    
    try
    {
      noLocal = requestMessage.getBooleanProperty(IKasqConstants.cPropertyConsumerNoLocal);
    }
    catch (Throwable e) {}
    mNoLocal = noLocal;
    
    try
    {
      selector = requestMessage.getStringProperty(IKasqConstants.cPropertyConsumerMessageSelector);
    }
    catch (Throwable e) {}
    mSelector = selector;
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
   * Get the consumer queue name
   * 
   * @return the consumer queue name
   */
  public String getConsumerQueue()
  {
    return mConsumerQueue;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("GetRequestProcessor::process() - IN");
    
    sLogger.debug("GetRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("GetRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      IKasqMessage message = null;
      IKasqDestination dest = null;
      int code = IKasqConstants.cPropertyResponseCode_Fail;
      String msg = "Failed to retrieve a message";
      
      // now we address the repository and locate the destination
      sLogger.debug("GetRequestProcessor::process() - Destination type is " + mDestinationType.toString());
      switch (mDestinationType)
      {
        case cQueue:
          dest = sRepository.locateQueue(mDestinationName);
          message = dest.getMatching(mNoLocal, mConsumerSession, mSelector);
          break;
        case cTopic:
          dest = sRepository.locateTopic(mDestinationName);
          message = dest.getMatching(mNoLocal, mConsumerSession, mSelector);
          break;
        default:
          break;
      }

      if (message == null)
      {
        sLogger.debug("GetRequestProcessor::process() - Failed to get a message from destination " + dest.getFormattedName());
        message = new KasqMessage();
        message.setJMSMessageID("ID:" + UniqueId.generate().toString());
        message.setJMSCorrelationID(mJmsMessageId);
      }
      else
      {
        sLogger.debug("GetRequestProcessor::process() - Got a message from destination " + dest.getFormattedName());
        code = IKasqConstants.cPropertyResponseCode_Okay;
        msg  = "";
        message.setStringProperty(IKasqConstants.cPropertyConsumerQueue, mConsumerQueue);
      }
      
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
      
      sLogger.debug("GetRequestProcessor::process() - Sending response message: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("GetRequestProcessor::process() - OUT, Result=" + result);
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