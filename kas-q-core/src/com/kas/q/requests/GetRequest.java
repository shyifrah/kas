package com.kas.q.requests;

import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

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
  public GetRequest(String destName, EDestinationType destType, boolean noLocal, String selector, String consQueue, String consSess)
  {
    super();
    mDestName = destName;
    mDestType = destType;
    mNoLocal = noLocal;
    mSelector = selector;
    mConsumerQueue = consQueue;
    mConsumerSession = consSess;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("GetRequest::setRequestProperties() - IN");
    
    try
    {
      // message destination
      requestMessage.setStringProperty(IKasqConstants.cPropertyDestinationName, mDestName);
      requestMessage.setIntProperty(IKasqConstants.cPropertyDestinationType, mDestType.ordinal());
      
      // message origin
      requestMessage.setStringProperty(IKasqConstants.cPropertyConsumerQueue, mConsumerQueue);
      requestMessage.setStringProperty(IKasqConstants.cPropertyConsumerSession, mConsumerSession);
      
      // filtering criteria
      requestMessage.setStringProperty(IKasqConstants.cPropertyConsumerMessageSelector, mSelector);
      requestMessage.setBooleanProperty(IKasqConstants.cPropertyConsumerNoLocal, mNoLocal);

    }
    catch (Throwable e)
    {
      mLogger.debug("GetRequest::setRequestProperties() - JMSException caught: ", e);
    }
    
    mLogger.debug("GetRequest::setRequestProperties() - OUT"); 
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cGet;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
