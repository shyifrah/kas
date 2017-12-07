package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;

public abstract class ARequest extends KasqMessage
{
  /***************************************************************************************************************
   *  
   */
  protected ILogger mLogger;
  protected ERequestType mType;
  
  /***************************************************************************************************************
   *  
   */
  ARequest(ERequestType type) throws JMSException
  {
    super();
    mLogger = LoggerFactory.getLogger(this.getClass());
    mLogger.debug("ARequest::ARequest - IN, Type=" + type.toString());
    mType   = type;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
