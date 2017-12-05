package com.kas.q.requests;

import com.kas.q.ext.IKasqMessage;

public class MetaRequest extends ARequest
{
  /***************************************************************************************************************
   *  
   */
  public MetaRequest()
  {
    super();
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setRequestProperties(IKasqMessage requestMessage)
  {
    mLogger.debug("MetaRequest::setRequestProperties() - IN/OUT");
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cMetaData;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return null;
  }
}
