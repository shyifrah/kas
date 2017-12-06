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
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    setBooleanProperty(IKasqConstants.cPropertyAdminMessage, true);
  }
}
