package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqMessage;

public abstract class ARequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   *  
   */
  protected ILogger mLogger;
  protected ERequestType mType;
  protected IKasqMessage mMessage;
  
  /***************************************************************************************************************
   *  
   */
  ARequest(ERequestType type) throws JMSException
  {
    this (type, new KasqMessage());
  }
  
  /***************************************************************************************************************
   *  
   */
  ARequest(ERequestType type, IKasqMessage message) throws JMSException
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mLogger.debug("ARequest::ARequest - IN, Type=" + type.toString());
    mType   = type;
    mMessage = message;
  }
  
  /***************************************************************************************************************
   *  
   */
  public IKasqMessage getRequestMessage()
  {
    mLogger.debug("ARequest::getRequestMessage() - IN");
    
    try
    {
      setup();
    }
    catch (Throwable e)
    {
      mLogger.debug("ARequest::getRequestMessage() - JMSException caught: ", e);
    }
    
    mLogger.debug("ARequest::getRequestMessage() - OUT, requestMessage=" + StringUtils.asPrintableString(mMessage));
    return mMessage;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return mType;
  }

  /***************************************************************************************************************
   *  
   */
  public abstract void setup();

  /***************************************************************************************************************
   *  
   */
  public abstract String toPrintableString(int level);
}
