package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IKasqConstants;

public class HaltRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public HaltRequest() throws JMSException
  {
    super(ERequestType.cHalt);
    
    mMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
    mMessage.setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setup()
  {
    mLogger.debug("HaltRequest::setup() - IN");
    
    try
    {
      mMessage.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, true);
    }
    catch (Throwable e)
    {
      mLogger.debug("HaltRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("HaltRequest::setup() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
