package com.kas.q.requests;

import com.kas.q.KasqQueue;
import com.kas.q.KasqTopic;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;

public class DefineRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  private String  mName;
  private EDestinationType mType;
  
  /***************************************************************************************************************
   *  
   */
  public DefineRequest(String name, EDestinationType type)
  {
    super();
    mName   = name;
    mType   = type;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("DefineRequest::setRequestProperties() - IN");
    
    try
    {
      IKasqDestination dest;
      if (mType == EDestinationType.cQueue)
        dest = new KasqQueue(mName, "");
      else
        dest = new KasqTopic(mName, "");
      
      requestMessage.setJMSDestination(dest);
    }
    catch (Throwable e)
    {
      mLogger.debug("DefineRequest::setRequestProperties() - JMSException caught: ", e);
    }
    
    mLogger.debug("DefineRequest::setRequestProperties() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cDefine;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
