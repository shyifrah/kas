package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;

public class QueryRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public QueryRequest(EDestinationType destType, String destName) throws JMSException
  {
    super(ERequestType.cQuery);
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    setBooleanProperty(IKasqConstants.cPropertyAdminMessage, true);
    setIntProperty(IKasqConstants.cPropertyDestinationType, destType.ordinal());
    if (destName != null)
      setStringProperty(IKasqConstants.cPropertyDestinationName, destName);
  }
}
