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
  private boolean mNoLocal;
  private String  mSelector;
  private String  mDestName;
  private EDestinationType mDestType;
  private String  mConsumerQueue;
  private String  mConsumerSession;
  
  /***************************************************************************************************************
   *  
   */
  public GetRequest(String destName, EDestinationType destType, boolean noLocal, String selector, String consQueue, String consSess) throws JMSException
  {
    super(ERequestType.cGet);
    mDestName = destName;
    mDestType = destType;
    mNoLocal = noLocal;
    mSelector = selector;
    mConsumerQueue = consQueue;
    mConsumerSession = consSess;
    
    mMessage.setJMSMessageID("ID:" + UniqueId.generate().toString());
    mMessage.setIntProperty(IKasqConstants.cPropertyRequestType, mType.ordinal());
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setup()
  {
    mLogger.debug("GetRequest::setup() - IN");
    
    try
    {
      // message destination
      mMessage.setStringProperty(IKasqConstants.cPropertyDestinationName, mDestName);
      mMessage.setIntProperty(IKasqConstants.cPropertyDestinationType, mDestType.ordinal());
      
      // message origin
      mMessage.setStringProperty(IKasqConstants.cPropertyConsumerQueue, mConsumerQueue);
      mMessage.setStringProperty(IKasqConstants.cPropertyConsumerSession, mConsumerSession);
      
      // filtering criteria
      mMessage.setStringProperty(IKasqConstants.cPropertyConsumerMessageSelector, mSelector);
      mMessage.setBooleanProperty(IKasqConstants.cPropertyConsumerNoLocal, mNoLocal);

    }
    catch (Throwable e)
    {
      mLogger.debug("GetRequest::setup() - JMSException caught: ", e);
    }
    
    mLogger.debug("GetRequest::setup() - OUT"); 
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
