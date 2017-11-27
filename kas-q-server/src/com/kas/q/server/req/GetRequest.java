package com.kas.q.server.req;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

final public class GetRequest extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(GetRequest.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private String           mDestinationName;
  private EDestinationType mDestinationType;
  private String           mJmsMessageId;
  
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
    sLogger.debug("GetRequest::process() - IN");
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("GetRequest::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      //
      // TODO: use the following message criteria to select the consumed message
      //
      // get the filtering criteria
      //String selector = "";
      //boolean noLocal = false;
      //try
      //{
      //  selector = request.getStringProperty(IKasqConstants.cPropertyMessageSelector);
      //  noLocal = request.getBooleanProperty(IKasqConstants.cPropertyNoLocal);
      //}
      //catch (Throwable e) {}
      
      IKasqMessage message = null;
      IKasqDestination dest = null;
      int code = IKasqConstants.cPropertyResponseCode_Fail;
      String msg = "Failed to retrieve a message";
      
      // now we address the repository and locate the destination
      sLogger.debug("LocateRequest::process() - Destination type is " + mDestinationType.toString());
      switch (mDestinationType)
      {
        case cQueue:
          dest = sRepository.locateQueue(mDestinationName);
          message = dest.getNoWait();
          break;
        case cTopic:
          dest = sRepository.locateTopic(mDestinationName);
          message = dest.getNoWait();
          break;
      }

      sLogger.debug("GetRequest::process() - Get from destination " + (message == null ? " did not " : " ") + "returned a message");
      if (message == null)
      {
        message = new KasqMessage();
      }
      else
      {
        code = IKasqConstants.cPropertyResponseCode_Okay;
        msg  = "";
      }
      
      message.setJMSCorrelationID(mJmsMessageId);
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
        
      sLogger.diag("GetRequest::process() - Sending to origin consumed message: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("GetRequest::process() - OUT, Result=" + result);
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
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(FromDest=").append(mDestinationType.toString()).append(":///").append(mDestinationName).append(")");
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
