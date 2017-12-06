package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnectionMetaData;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqServer;

final public class MetaRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(MetaRequestProcessor.class);
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage mMessage;
  private String  mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code MetaRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  MetaRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
    String  jmsMsgId = null;
    try
    {
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received MetaRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid MetaRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received MetaRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid MetaRequest: JMS message ID is empty string");
    }
    
    mJmsMessageId = jmsMsgId;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("MetaRequestProcessor::process() - IN");
    
    sLogger.debug("MetaRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("MetaRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      ProductVersion version = KasqServer.getInstance().getVersion();
      ConnectionMetaData metaData = new KasqConnectionMetaData(version);
      
      KasqMessage message = new KasqMessage();
      message.setJMSMessageID("ID:" + UniqueId.generate().toString());
      message.setJMSCorrelationID(mJmsMessageId);
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, IKasqConstants.cPropertyResponseCode_Okay);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, "");
      message.setObjectProperty(IKasqConstants.cPropertyMetaData, metaData);
        
      sLogger.debug("MetaRequestProcessor::process() - Sending response message: " + message.toPrintableString(0));
      handler.send(message);
      result = true;
    }
    
    sLogger.debug("MetaRequestProcessor::process() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cMetaData;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
