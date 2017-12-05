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
  private String  mDestName;
  private EDestinationType mDestType;
  
  /***************************************************************************************************************
   *  
   */
  public DefineRequest(String destName, EDestinationType destType) throws JMSException
  {
    super(ERequestType.cDefine);
    mDestName = destName;
    mDestType = destType;
    
    mMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
    mMessage.setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setup()
  {
    mLogger.debug("DefineRequest::setup() - IN");
    
    try
    {
      IKasqDestination dest;
      if (mDestType == EDestinationType.cQueue)
        dest = new KasqQueue(mDestName, "");
      else
        dest = new KasqTopic(mDestName, "");
      
      mMessage.setJMSDestination(dest);
    }
    catch (Throwable e)
    {
      mLogger.debug("DefineRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("DefineRequest::setup() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
