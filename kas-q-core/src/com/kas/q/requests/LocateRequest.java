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
  private String  mDestName;
  private EDestinationType mDestType;
  
  /***************************************************************************************************************
   *  
   */
  public LocateRequest(String destName, EDestinationType destType) throws JMSException
  {
    super(ERequestType.cLocate);
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
    mLogger.debug("LocateRequest::setup() - IN");
    
    try
    {
      mMessage.setStringProperty(IKasqConstants.cPropertyDestinationName, mDestName);
      mMessage.setIntProperty(IKasqConstants.cPropertyDestinationType, mDestType.ordinal());
    }
    catch (Throwable e)
    {
      mLogger.debug("LocateRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("LocateRequest::setup() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
