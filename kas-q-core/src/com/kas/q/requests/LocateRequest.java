package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;

public class LocateRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public LocateRequest(String destName, EDestinationType destType) throws JMSException
  {
    super(ERequestType.cLocate);

    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    setStringProperty(IKasqConstants.cPropertyDestinationName, destName);
    setIntProperty(IKasqConstants.cPropertyDestinationType, destType.ordinal());
  }
}
