package com.kas.q.requests;

import javax.jms.JMSException;
import com.kas.infra.base.UniqueId;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;

public class DefineRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public DefineRequest(String destName, EDestinationType destType) throws JMSException
  {
    super(ERequestType.cDefine);
    
    setJMSMessageID("ID:" + UniqueId.generate().toString());
    setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
    
    IKasqDestination dest;
    if (destType == EDestinationType.cQueue)
      dest = new KasqQueue(destName, "");
    else if (destType == EDestinationType.cTopic)
      dest = new KasqTopic(destName, "");
    else
      throw new JMSException("Failed to define destination: Invalid destination type: [" + destType + "]");
    
    setJMSDestination(dest);
  }
}
