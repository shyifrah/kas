package com.kas.q.requests;

import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class LocateRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  private String  mName;
  private EDestinationType mType;
  
  /***************************************************************************************************************
   *  
   */
  public LocateRequest(String name, EDestinationType type)
  {
    super();
    mName = name;
    mType = type;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("LocateRequest::setRequestProperties() - IN");
    
    try
    {
      requestMessage.setStringProperty(IKasqConstants.cPropertyDestinationName, mName);
      requestMessage.setIntProperty(IKasqConstants.cPropertyDestinationType, mType.ordinal());
    }
    catch (Throwable e)
    {
      mLogger.debug("LocateRequest::setRequestProperties() - JMSException caught: ", e);
    }
    
    mLogger.debug("LocateRequest::setRequestProperties() - OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cLocate;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
