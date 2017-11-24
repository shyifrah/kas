package com.kas.q.server.req;

import java.io.IOException;
import javax.jms.Destination;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

final public class DefineRequest extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(DefineRequest.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqDestination mJmsDestination;
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
    Destination dest = null;
    String  jmsMsgId = null;
    try
    {
      dest = requestMessage.getJMSDestination();
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    if (dest == null)
    {
      sLogger.warn("Received DefineRequest with invalid JMS Destination: dest=[" + StringUtils.asString(dest) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: null JMS Destination");
    }
    
    if (!(dest instanceof IKasqDestination))
    {
      sLogger.warn("Received DefineRequest with non-KAS/Q JMS Destination: dest=[" + StringUtils.asString(dest) + "]");
      throw new IllegalArgumentException("Invalid DefineRequest: JMS Destination not managed by KAS/Q");
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
    
    mJmsDestination = (IKasqDestination)dest;
    mJmsMessageId = jmsMsgId;
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
   * Get the JMS destination
   * 
   * @return the JMS destination
   */
  public Destination getJmsDestination()
  {
    return mJmsDestination;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("DefineRequest::process() - IN");
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("DefineRequest::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      int code = IKasqConstants.cPropertyResponseCode_Fail;
      String msg  = "";
      
      boolean defined = sRepository.define((IKasqDestination)mJmsDestination);
      if (defined)
      {
        code = IKasqConstants.cPropertyResponseCode_Okay;
      }
      else
      {
        msg = mJmsDestination.getType() + " with name " + mJmsDestination.getName() + " already exists";
      }
      
      IKasqMessage message = new KasqMessage();
      message.setJMSDestination(mJmsDestination);
      message.setJMSCorrelationID(mJmsMessageId);
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
        
      sLogger.diag("DefineRequest::process() - Sending to origin response: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("DefineRequest::process() - OUT, Result=" + result);
    return result;
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
      .append("(Dest=").append(mJmsDestination.getFormattedName()).append(")");
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
      .append(pad).append("  Destintation=(").append(mJmsDestination.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  Request MessageId=(").append(mJmsMessageId).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
