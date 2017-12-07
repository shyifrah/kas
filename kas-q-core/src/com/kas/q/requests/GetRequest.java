package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;

public class GetRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public GetRequest(String destName, EDestinationType destType, boolean noLocal, String selector, String consQueue, String consSess) throws JMSException
  {
    super(ERequestType.cGet);
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    
    // message destination
    setStringProperty(IKasqConstants.cPropertyDestinationName, destName);
    setIntProperty(IKasqConstants.cPropertyDestinationType, destType.ordinal());
    
    // message origin
    setStringProperty(IKasqConstants.cPropertyConsumerQueue, consQueue);
    setStringProperty(IKasqConstants.cPropertyConsumerSession, consSess);
    
    // filtering criteria
    setStringProperty(IKasqConstants.cPropertyConsumerMessageSelector, selector);
    setBooleanProperty(IKasqConstants.cPropertyConsumerNoLocal, noLocal);
  }
}
