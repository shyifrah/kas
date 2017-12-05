package com.kas.q.requests;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public abstract class ARequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   *  
   */
  protected ILogger mLogger;
  
  /***************************************************************************************************************
   *  
   */
  ARequest()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /***************************************************************************************************************
   *  
   */
  public abstract ERequestType getRequestType();
  
  /***************************************************************************************************************
   *  
   */
  public abstract void setRequestProperties(IKasqMessage requestMessage);

  /***************************************************************************************************************
   *  
   */
  public IKasqMessage createRequestMessage()
  {
    mLogger.debug("ARequest::createRequestMessage() - IN");
    
    IKasqMessage requestMessage = null;
    
    if (getRequestType() == ERequestType.cPut)
    {
      setRequestProperties(requestMessage);
    }
    else
    {
      try
      {
        requestMessage = new KasqMessage();
        requestMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
        requestMessage.setIntProperty(IKasqConstants.cPropertyRequestType, getRequestType().ordinal());
        
        setRequestProperties(requestMessage);
      }
      catch (Throwable e)
      {
        mLogger.debug("ARequest::createRequestMessage() - JMSException caught: ", e);
      }
    }
    
    mLogger.debug("ARequest::createRequestMessage() - OUT, requestMessage=" + StringUtils.asPrintableString(requestMessage));
    return requestMessage;
  }

  public abstract String toPrintableString(int level);
}
