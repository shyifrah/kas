package com.kas.q.requests;

import com.kas.q.ext.IKasqMessage;

public class HaltRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public HaltRequest()
  {
    super();
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("HaltRequest::setRequestProperties() - IN/OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cHalt;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
