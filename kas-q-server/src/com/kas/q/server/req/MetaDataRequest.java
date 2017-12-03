package com.kas.q.server.req;

import java.io.IOException;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ProductVersion;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqConnectionMetaData;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqServer;

final public class MetaDataRequest extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(MetaDataRequest.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mJmsMessageId;
  
  /***************************************************************************************************************
   * Construct a {@code MetaDataRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  MetaDataRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    String  jmsMsgId = null;
    try
    {
      jmsMsgId = requestMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    if (jmsMsgId == null)
    {
      sLogger.warn("Received MetaDataRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid MetaDataRequest: null JMS message ID");
    }
    
    if (jmsMsgId.length() == 0)
    {
      sLogger.warn("Received MetaDataRequest with invalid JMS Message ID: id=[" + StringUtils.asString(jmsMsgId) + "]");
      throw new IllegalArgumentException("Invalid MetaDataRequest: JMS message ID is empty string");
    }
    
    mJmsMessageId = jmsMsgId;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("MetaDataRequest::process() - IN");
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("MetaDataRequest::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      ProductVersion version = KasqServer.getInstance().getVersion();
      ConnectionMetaData metaData = new KasqConnectionMetaData(version);
      
      KasqMessage message = new KasqMessage();
      
      message.setJMSCorrelationID(mJmsMessageId);
      message.setIntProperty(IKasqConstants.cPropertyResponseCode, IKasqConstants.cPropertyResponseCode_Okay);
      message.setStringProperty(IKasqConstants.cPropertyResponseMessage, "");
      message.setObjectProperty(IKasqConstants.cPropertyMetaData, metaData);
        
      sLogger.diag("GetRequest::process() -  Sending to origin response message: " + message.toPrintableString(0));
      handler.send(message);
      
      result = true;
    }
    
    sLogger.debug("MetaDataRequest::process() - OUT, Result=" + result);
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
  public String toString()
  {
    return name();
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
