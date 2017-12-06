package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.IKasqConstants;

public class MetaRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public MetaRequest() throws JMSException
  {
    super(ERequestType.cMetaData);
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
  }
}
